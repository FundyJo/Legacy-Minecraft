package wily.legacy.minigame.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.phys.Vec3;
import wily.legacy.Legacy4J;
import wily.legacy.fantasy.MinigameMapTemplateLoader;
import wily.legacy.fantasy.RuntimeWorldHandle;
import wily.legacy.minigame.Minigame;
import wily.legacy.minigame.grf.GrfMap;
import wily.legacy.minigame.networking.S2CLeaderboardPayload;
import wily.legacy.minigame.networking.S2CMapTransitionPayload;

import java.util.*;

/**
 * Tumble minigame controller: Players fight on diminishing snow floors.
 * Players throw snowballs to break blocks under enemies, trying to make them fall.
 * The last player remaining on a platform wins.
 *
 * Loads the arena from a .mcsave template pack if one is available,
 * otherwise falls back to procedurally generating snow floor layers.
 */
public class TumbleMinigameController extends AbstractMinigameController<TumbleMinigameController> {

    private static final int GAME_START_COUNTDOWN = 20 * 5;
    private static final int FLOOR_LAYER_TICKS = 20 * 45;
    private static final int FLOOR_SIZE = 17;
    private static final int FLOOR_BASE_Y = 64;
    private static final int LAYERS = 3;

    private final Map<UUID, PlayerTumbleState> playerStates = new HashMap<>();
    private TumblePhase phase = TumblePhase.COUNTDOWN;
    private int phaseTimer = 0;
    private int currentRound = 0;
    private int currentLayer = 0;
    private final RandomSource random = new XoroshiroRandomSource(System.currentTimeMillis());
    private RuntimeWorldHandle worldHandle = null;

    public enum TumblePhase { COUNTDOWN, PLAYING, ROUND_END }

    public TumbleMinigameController(ServerLevel level) {
        super(level);
    }

    @Override
    protected void onStart() {
        phase = TumblePhase.COUNTDOWN;
        phaseTimer = 0;
        currentRound = 0;
        playerStates.clear();

        ResourceLocation mapId = data.mapId();
        GrfMap grfMap = GrfMap.load(mapId);
        ResourceLocation templateId = grfMap != null ? grfMap.getTemplatePackId() : mapId;

        broadcastToAllPlayers(new S2CMapTransitionPayload(mapId, Minigame.TUMBLE.getName(), 60));

        if (MinigameMapTemplateLoader.hasTemplate(level.getServer(), templateId)) {
            RuntimeWorldHandle handle = MinigameMapTemplateLoader.loadTemplateHandle(level.getServer(), templateId);
            if (handle != null) {
                worldHandle = handle;
                this.level = handle.asWorld();
                Legacy4J.LOGGER.info("[Legacy4J Tumble] Loaded template world '{}' for map '{}'", templateId, mapId);
            }
        }

        Vec3 spawnVec = (grfMap != null && grfMap.getSpawnPos() != null)
                ? grfMap.getSpawnPos()
                : new Vec3(level.getSharedSpawnPos().getX(), FLOOR_BASE_Y + (LAYERS - 1) * 8 + 1,
                           level.getSharedSpawnPos().getZ());

        for (UUID uuid : players) {
            playerStates.put(uuid, new PlayerTumbleState());
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
            if (player != null) {
                player.teleportTo(level, spawnVec.x, spawnVec.y, spawnVec.z, player.getYRot(), player.getXRot());
                setupPlayer(player);
            }
        }

        // Only build procedural arena if no template was loaded
        if (worldHandle == null) {
            buildArena();
        }
    }

    private void setupPlayer(ServerPlayer player) {
        player.setGameMode(GameType.SURVIVAL);
        player.getInventory().clearContent();
        ItemStack snowballs = new ItemStack(Items.SNOWBALL, 16);
        player.getInventory().setItem(0, snowballs);
        player.getAbilities().mayfly = false;
        player.onUpdateAbilities();
        player.setHealth(player.getMaxHealth());
    }

    private void buildArena() {
        int centerX = level.getSharedSpawnPos().getX();
        int centerZ = level.getSharedSpawnPos().getZ();
        for (int layer = 0; layer < LAYERS; layer++) {
            int y = FLOOR_BASE_Y + layer * 8;
            buildFloor(centerX, y, centerZ);
        }
    }

    private void buildFloor(int centerX, int y, int centerZ) {
        int half = FLOOR_SIZE / 2;
        for (int x = centerX - half; x <= centerX + half; x++) {
            for (int z = centerZ - half; z <= centerZ + half; z++) {
                level.setBlock(new BlockPos(x, y, z), Blocks.SNOW_BLOCK.defaultBlockState(), 3);
            }
        }
    }

    @Override
    protected void onEnd() {
        for (UUID uuid : players) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
            if (player != null) player.setGameMode(GameType.SURVIVAL);
        }
        if (worldHandle != null) {
            worldHandle.delete();
            worldHandle = null;
            Legacy4J.LOGGER.info("[Legacy4J Tumble] Deleted Tumble template world.");
        }
    }

    @Override
    protected void onTick() {
        phaseTimer++;
        switch (phase) {
            case COUNTDOWN -> {
                int remaining = GAME_START_COUNTDOWN - phaseTimer;
                if (remaining <= 0) {
                    startRound();
                } else if (remaining % 20 == 0) {
                    broadcastTitle(Component.literal(String.valueOf(remaining / 20)),
                            Component.translatable("legacy.minigame.tumble.ready"));
                }
            }
            case PLAYING -> {
                if (phaseTimer % FLOOR_LAYER_TICKS == 0 && currentLayer < LAYERS - 1) {
                    removeCurrentLayer();
                }
                checkFallenPlayers();
                if (phaseTimer % 20 == 0) {
                    broadcastLeaderboard();
                    long active = playerStates.values().stream().filter(s -> !s.eliminated).count();
                    if (active <= 1) endRound();
                }
            }
            case ROUND_END -> {
                if (phaseTimer >= 20 * 5) {
                    if (currentRound < data.rounds()) {
                        startRound();
                    } else {
                        end();
                    }
                }
            }
        }
    }

    private void startRound() {
        currentRound++;
        currentLayer = 0;
        phase = TumblePhase.PLAYING;
        phaseTimer = 0;
        playerStates.values().forEach(s -> s.eliminated = false);
        if (worldHandle == null) {
            buildArena();
        }
        int i = 0;
        for (UUID uuid : players) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
            if (player != null) {
                double angle = (i++ * 2 * Math.PI) / Math.max(players.size(), 1);
                double x = level.getSharedSpawnPos().getX() + Math.cos(angle) * 5;
                double z = level.getSharedSpawnPos().getZ() + Math.sin(angle) * 5;
                player.teleportTo(level, x, FLOOR_BASE_Y + (LAYERS - 1) * 8 + 1, z, player.getYRot(), player.getXRot());
                setupPlayer(player);
            }
        }
        broadcastTitle(Component.translatable("legacy.minigame.tumble.round", currentRound),
                Component.translatable("legacy.minigame.tumble.survive"));
    }

    private void removeCurrentLayer() {
        currentLayer++;
        int centerX = level.getSharedSpawnPos().getX();
        int centerZ = level.getSharedSpawnPos().getZ();
        int y = FLOOR_BASE_Y + (LAYERS - currentLayer) * 8;
        int half = FLOOR_SIZE / 2;
        for (int x = centerX - half; x <= centerX + half; x++) {
            for (int z = centerZ - half; z <= centerZ + half; z++) {
                level.removeBlock(new BlockPos(x, y, z), false);
            }
        }
        broadcastTitle(Component.translatable("legacy.minigame.tumble.layer_removed"),
                Component.translatable("legacy.minigame.tumble.watch_out"));
    }

    private void checkFallenPlayers() {
        for (UUID uuid : players) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
            PlayerTumbleState state = playerStates.get(uuid);
            if (player == null || state == null || state.eliminated) continue;
            if (player.getY() < FLOOR_BASE_Y - 10) {
                state.eliminated = true;
                state.deaths++;
                player.teleportTo(level,
                        level.getSharedSpawnPos().getX(),
                        FLOOR_BASE_Y + (LAYERS - 1) * 8 + 10,
                        level.getSharedSpawnPos().getZ(),
                        0, 0);
                player.setGameMode(GameType.SPECTATOR);
                player.sendSystemMessage(Component.translatable("legacy.minigame.tumble.eliminated"));
            }
        }
    }

    private void endRound() {
        phase = TumblePhase.ROUND_END;
        phaseTimer = 0;
        UUID roundWinner = playerStates.entrySet().stream()
                .filter(e -> !e.getValue().eliminated)
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
        if (roundWinner != null) {
            playerStates.get(roundWinner).roundWins++;
            ServerPlayer winner = level.getServer().getPlayerList().getPlayer(roundWinner);
            String winnerName = winner != null ? winner.getName().getString() : "Unknown";
            broadcastTitle(Component.translatable("legacy.minigame.tumble.round_winner", winnerName),
                    Component.empty());
            MinigamesController.addStats(roundWinner, Minigame.TUMBLE, 100);
        }
        broadcastLeaderboard();
    }

    private void broadcastLeaderboard() {
        List<Map.Entry<UUID, PlayerTumbleState>> sorted = playerStates.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<UUID, PlayerTumbleState> e) -> -e.getValue().roundWins))
                .toList();
        S2CLeaderboardPayload.LeaderboardEntry[] entries = new S2CLeaderboardPayload.LeaderboardEntry[sorted.size()];
        for (int i = 0; i < sorted.size(); i++) {
            UUID uuid = sorted.get(i).getKey();
            PlayerTumbleState state = sorted.get(i).getValue();
            ServerPlayer sp = level.getServer().getPlayerList().getPlayer(uuid);
            String name = sp != null ? sp.getName().getString() : uuid.toString();
            entries[i] = new S2CLeaderboardPayload.LeaderboardEntry(uuid, name, state.roundWins, i + 1);
        }
        broadcastToPlayers(new S2CLeaderboardPayload(entries));
    }

    @Override
    public Minigame<TumbleMinigameController> getMinigame() {
        return Minigame.TUMBLE;
    }

    private static class PlayerTumbleState {
        int roundWins = 0;
        int deaths = 0;
        boolean eliminated = false;
    }
}

