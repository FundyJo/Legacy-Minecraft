package wily.legacy.minigame.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import wily.legacy.Legacy4J;
import wily.legacy.fantasy.MinigameMapTemplateLoader;
import wily.legacy.fantasy.RuntimeWorldHandle;
import wily.legacy.minigame.Minigame;
import wily.legacy.minigame.grf.GrfMap;
import wily.legacy.minigame.grf.element.FistfightFlag;
import wily.legacy.minigame.networking.S2CLeaderboardPayload;
import wily.legacy.minigame.networking.S2CMapTransitionPayload;

import java.util.*;

/**
 * Fistfight minigame controller: PvP brawl where players fight with their fists.
 * Players are knocked off platforms and must not fall below the void threshold.
 * Last player standing or player with most kills wins.
 *
 * Loads the arena from a .mcsave template pack and uses GRF FistfightFlag elements
 * for spawn positions and kill/death zones.
 */
public class FistfightMinigameController extends AbstractMinigameController<FistfightMinigameController> {

    private static final int GAME_START_COUNTDOWN = 20 * 5;
    private static final int ROUND_DURATION_TICKS = 20 * 60 * 3;

    private final Map<UUID, PlayerFistfightState> playerStates = new HashMap<>();
    private FistfightPhase phase = FistfightPhase.COUNTDOWN;
    private int phaseTimer = 0;
    private int currentRound = 0;
    private RuntimeWorldHandle worldHandle = null;
    private List<FistfightFlag> spawnFlags = new ArrayList<>();

    public enum FistfightPhase { COUNTDOWN, FIGHTING, ROUND_END }

    public FistfightMinigameController(ServerLevel level) {
        super(level);
    }

    @Override
    protected void onStart() {
        phase = FistfightPhase.COUNTDOWN;
        phaseTimer = 0;
        currentRound = 0;
        playerStates.clear();

        ResourceLocation mapId = data.mapId();
        GrfMap grfMap = GrfMap.load(mapId);
        ResourceLocation templateId = grfMap != null ? grfMap.getTemplatePackId() : mapId;

        broadcastToAllPlayers(new S2CMapTransitionPayload(mapId, Minigame.FISTFIGHT.getName(), 60));

        if (MinigameMapTemplateLoader.hasTemplate(level.getServer(), templateId)) {
            RuntimeWorldHandle handle = MinigameMapTemplateLoader.loadTemplateHandle(level.getServer(), templateId);
            if (handle != null) {
                worldHandle = handle;
                this.level = handle.asWorld();
                Legacy4J.LOGGER.info("[Legacy4J Fistfight] Loaded template world '{}' for map '{}'", templateId, mapId);
            }
        }

        // Load spawn positions from GRF SPAWN flags
        if (grfMap != null) {
            spawnFlags = grfMap.getFistfightFlags().stream()
                    .filter(f -> f.type() == FistfightFlag.FlagType.SPAWN)
                    .toList();
        }

        Vec3 baseSpawn = (grfMap != null && grfMap.getSpawnPos() != null)
                ? grfMap.getSpawnPos()
                : new Vec3(level.getSharedSpawnPos().getX(), level.getSharedSpawnPos().getY() + 1,
                           level.getSharedSpawnPos().getZ());

        int idx = 0;
        for (UUID uuid : players) {
            playerStates.put(uuid, new PlayerFistfightState());
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
            if (player != null) {
                Vec3 spawn = getSpawnPosition(idx, baseSpawn);
                player.teleportTo(level, spawn.x, spawn.y, spawn.z, player.getYRot(), player.getXRot());
                setupPlayer(player);
            }
            idx++;
        }
    }

    private Vec3 getSpawnPosition(int playerIndex, Vec3 baseSpawn) {
        if (!spawnFlags.isEmpty()) {
            FistfightFlag flag = spawnFlags.get(playerIndex % spawnFlags.size());
            BlockPos pos = flag.position();
            return new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        }
        double angle = (playerIndex * 2 * Math.PI) / Math.max(players.size(), 1);
        return new Vec3(baseSpawn.x + Math.cos(angle) * 5, baseSpawn.y, baseSpawn.z + Math.sin(angle) * 5);
    }

    private void setupPlayer(ServerPlayer player) {
        player.setGameMode(GameType.ADVENTURE);
        player.getInventory().clearContent();
        player.getInventory().setItem(0, new ItemStack(Items.WOODEN_SWORD));
        player.getAbilities().mayfly = false;
        player.onUpdateAbilities();
        player.setHealth(player.getMaxHealth());
    }

    @Override
    protected void onEnd() {
        for (UUID uuid : players) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
            if (player != null) player.setGameMode(GameType.SURVIVAL);
        }
        broadcastLeaderboard();
        if (worldHandle != null) {
            worldHandle.delete();
            worldHandle = null;
            Legacy4J.LOGGER.info("[Legacy4J Fistfight] Deleted Fistfight template world.");
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
                            Component.translatable("legacy.minigame.fistfight.ready"));
                }
            }
            case FIGHTING -> {
                if (phaseTimer >= ROUND_DURATION_TICKS) {
                    endRound();
                } else if (phaseTimer % 20 == 0) {
                    broadcastLeaderboard();
                    long alive = playerStates.values().stream().filter(s -> !s.eliminated).count();
                    if (alive <= 1) {
                        endRound();
                    }
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
        phase = FistfightPhase.FIGHTING;
        phaseTimer = 0;
        playerStates.values().forEach(s -> s.eliminated = false);
        Vec3 baseSpawn = new Vec3(level.getSharedSpawnPos().getX(), level.getSharedSpawnPos().getY() + 1,
                level.getSharedSpawnPos().getZ());
        int playerIndex = 0;
        for (UUID uuid : players) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
            if (player != null) {
                player.setHealth(player.getMaxHealth());
                Vec3 spawn = getSpawnPosition(playerIndex, baseSpawn);
                player.teleportTo(level, spawn.x, spawn.y, spawn.z, player.getYRot(), player.getXRot());
            }
            playerIndex++;
        }
        broadcastTitle(Component.translatable("legacy.minigame.fistfight.round", currentRound),
                Component.translatable("legacy.minigame.fistfight.fight"));
    }

    private void endRound() {
        phase = FistfightPhase.ROUND_END;
        phaseTimer = 0;
        UUID roundWinner = playerStates.entrySet().stream()
                .filter(e -> !e.getValue().eliminated)
                .max(Comparator.comparingInt(e -> e.getValue().kills))
                .map(Map.Entry::getKey)
                .orElse(null);
        if (roundWinner != null) {
            playerStates.get(roundWinner).roundWins++;
            ServerPlayer winner = level.getServer().getPlayerList().getPlayer(roundWinner);
            String winnerName = winner != null ? winner.getName().getString() : "Unknown";
            broadcastTitle(Component.translatable("legacy.minigame.fistfight.round_winner", winnerName),
                    Component.empty());
        }
        broadcastLeaderboard();
    }

    public void onPlayerDeath(ServerPlayer killed, DamageSource source) {
        if (phase != FistfightPhase.FIGHTING) return;
        PlayerFistfightState killedState = playerStates.get(killed.getUUID());
        if (killedState != null) {
            killedState.deaths++;
            killedState.eliminated = true;
        }
        if (source.getEntity() instanceof ServerPlayer killer && killer != killed) {
            PlayerFistfightState killerState = playerStates.get(killer.getUUID());
            if (killerState != null) killerState.kills++;
        }
        long alive = playerStates.values().stream().filter(s -> !s.eliminated).count();
        if (alive <= 1) endRound();
    }

    private void broadcastLeaderboard() {
        List<Map.Entry<UUID, PlayerFistfightState>> sorted = playerStates.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<UUID, PlayerFistfightState> e) -> -e.getValue().kills))
                .toList();
        S2CLeaderboardPayload.LeaderboardEntry[] entries = new S2CLeaderboardPayload.LeaderboardEntry[sorted.size()];
        for (int i = 0; i < sorted.size(); i++) {
            UUID uuid = sorted.get(i).getKey();
            PlayerFistfightState state = sorted.get(i).getValue();
            ServerPlayer sp = level.getServer().getPlayerList().getPlayer(uuid);
            String name = sp != null ? sp.getName().getString() : uuid.toString();
            entries[i] = new S2CLeaderboardPayload.LeaderboardEntry(uuid, name, state.kills, i + 1);
        }
        broadcastToPlayers(new S2CLeaderboardPayload(entries));
    }

    @Override
    public Minigame<FistfightMinigameController> getMinigame() {
        return Minigame.FISTFIGHT;
    }

    private static class PlayerFistfightState {
        int kills = 0;
        int deaths = 0;
        int roundWins = 0;
        boolean eliminated = false;
    }
}

