package wily.legacy.minigame.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import wily.legacy.Legacy4J;
import wily.legacy.minigame.Minigame;
import wily.legacy.minigame.networking.S2CGlideCheckpointPayload;
import wily.legacy.minigame.networking.S2CLeaderboardPayload;
import wily.legacy.fantasy.Fantasy;
import wily.legacy.fantasy.RuntimeWorldConfig;
import wily.legacy.fantasy.RuntimeWorldHandle;

import java.util.*;

/**
 * Glide minigame controller: Elytra racing through checkpoints.
 * Players fly through checkpoints to complete a course as fast as possible.
 *
 * Uses the custom Fantasy API to create a temporary runtime world
 * for the Glide map, which is deleted when the game ends.
 */
public class GlideMinigameController extends AbstractMinigameController<GlideMinigameController> {

    private static final int GAME_START_COUNTDOWN = 20 * 5;
    private static final int MAX_GAME_TICKS = 20 * 60 * 10;

    private final Map<UUID, PlayerGlideState> playerStates = new HashMap<>();
    private List<BlockPos> checkpoints = new ArrayList<>();
    private GlidePhase phase = GlidePhase.WAITING;
    private int phaseTimer = 0;
    private int finishedCount = 0;

    // Fantasy API world handle for temporary dimension management
    private RuntimeWorldHandle worldHandle = null;

    public enum GlidePhase { WAITING, COUNTDOWN, RACING, FINISHED }

    public GlideMinigameController(ServerLevel level) {
        super(level);
    }

    @Override
    protected void onStart() {
        phase = GlidePhase.COUNTDOWN;
        phaseTimer = 0;
        finishedCount = 0;
        playerStates.clear();
        checkpoints = GrfMap.loadCheckpoints(data.mapId());

        for (UUID uuid : players) {
            playerStates.put(uuid, new PlayerGlideState(checkpoints.size()));
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
            if (player != null) {
                setupPlayer(player);
            }
        }

        try {
            Fantasy fantasy = Fantasy.get(level.getServer());
            RuntimeWorldConfig config = new RuntimeWorldConfig()
                    .setDimensionType(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION_TYPE, Legacy4J.createModLocation("glide")))
                    .setGenerator(level.getServer().getWorldData().worldGenOptions().dimensions().getDimension(net.minecraft.world.level.dimension.BuiltinDimensionTypes.OVERWORLD).generator())
                    .setShouldTickTime(false);
            worldHandle = fantasy.openTemporaryWorld(config);
            Legacy4J.LOGGER.info("[Legacy4J Glide] Opened temporary world for Glide map {}", data.mapId());
        } catch (Exception e) {
            Legacy4J.LOGGER.warn("[Legacy4J Glide] Could not create Fantasy world: {}", e.getMessage());
        }
    }

    private void setupPlayer(ServerPlayer player) {
        player.setGameMode(GameType.ADVENTURE);
        ItemStack elytra = new ItemStack(Items.ELYTRA);
        player.setItemSlot(EquipmentSlot.CHEST, elytra);
    }

    @Override
    protected void onEnd() {
        playerStates.clear();
        checkpoints.clear();
        if (worldHandle != null) {
            worldHandle.delete();
            worldHandle = null;
            Legacy4J.LOGGER.info("[Legacy4J Glide] Deleted temporary Glide world.");
        }
    }

    @Override
    protected void onTick() {
        phaseTimer++;
        switch (phase) {
            case COUNTDOWN -> {
                int remaining = GAME_START_COUNTDOWN - phaseTimer;
                if (remaining <= 0) {
                    phase = GlidePhase.RACING;
                    phaseTimer = 0;
                    broadcastTitle(Component.translatable("legacy.minigame.glide.go"),
                            Component.translatable("legacy.minigame.glide.fly_through"));
                } else if (remaining % 20 == 0) {
                    broadcastTitle(Component.literal(String.valueOf(remaining / 20)),
                            Component.translatable("legacy.minigame.glide.ready"));
                }
            }
            case RACING -> {
                if (phaseTimer > MAX_GAME_TICKS) {
                    end();
                    return;
                }
                checkPlayerCheckpoints();
                if (phaseTimer % 20 == 0) {
                    broadcastLeaderboard();
                }
            }
        }
    }

    private void checkPlayerCheckpoints() {
        for (UUID uuid : players) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
            PlayerGlideState state = playerStates.get(uuid);
            if (player == null || state == null || state.finished) continue;

            int nextCheckpoint = state.nextCheckpoint;
            if (nextCheckpoint >= checkpoints.size()) {
                finishPlayer(player, state);
                continue;
            }

            BlockPos cpPos = checkpoints.get(nextCheckpoint);
            AABB checkpointBox = new AABB(cpPos).inflate(3.0);
            if (checkpointBox.contains(player.position())) {
                state.nextCheckpoint++;
                state.lastCheckpointTimeMs = System.currentTimeMillis();
                wily.factoryapi.base.network.CommonNetwork.sendToPlayer(player,
                        new S2CGlideCheckpointPayload(state.nextCheckpoint, checkpoints.size(), state.lastCheckpointTimeMs - state.startTimeMs));

                if (state.nextCheckpoint >= checkpoints.size()) {
                    finishPlayer(player, state);
                }
            }
        }
    }

    private void finishPlayer(ServerPlayer player, PlayerGlideState state) {
        state.finished = true;
        state.finishTimeMs = System.currentTimeMillis();
        finishedCount++;
        long elapsed = state.finishTimeMs - state.startTimeMs;
        broadcastTitle(Component.translatable("legacy.minigame.glide.finished", finishedCount),
                Component.translatable("legacy.minigame.glide.time", elapsed / 1000.0));
        MinigamesController.addStats(player.getUUID(), Minigame.GLIDE, (int)(MAX_GAME_TICKS - (state.finishTimeMs - state.startTimeMs) / 50));

        if (finishedCount >= players.size()) {
            end();
        }
    }

    private void broadcastLeaderboard() {
        List<Map.Entry<UUID, PlayerGlideState>> sorted = playerStates.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<UUID, PlayerGlideState> e) -> -e.getValue().nextCheckpoint)
                        .thenComparingLong(e -> e.getValue().lastCheckpointTimeMs))
                .toList();

        S2CLeaderboardPayload.LeaderboardEntry[] entries = new S2CLeaderboardPayload.LeaderboardEntry[sorted.size()];
        for (int i = 0; i < sorted.size(); i++) {
            UUID uuid = sorted.get(i).getKey();
            PlayerGlideState state = sorted.get(i).getValue();
            ServerPlayer sp = level.getServer().getPlayerList().getPlayer(uuid);
            String name = sp != null ? sp.getName().getString() : uuid.toString();
            entries[i] = new S2CLeaderboardPayload.LeaderboardEntry(uuid, name, state.nextCheckpoint, i + 1);
        }
        broadcastToPlayers(new S2CLeaderboardPayload(entries));
    }

    @Override
    public Minigame<GlideMinigameController> getMinigame() {
        return Minigame.GLIDE;
    }

    private static class PlayerGlideState {
        int nextCheckpoint = 0;
        int totalCheckpoints;
        long startTimeMs = System.currentTimeMillis();
        long lastCheckpointTimeMs = System.currentTimeMillis();
        long finishTimeMs = 0;
        boolean finished = false;

        PlayerGlideState(int totalCheckpoints) {
            this.totalCheckpoints = totalCheckpoints;
        }
    }
}
