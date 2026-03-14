package wily.legacy.minigame.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import wily.legacy.Legacy4J;
import wily.legacy.fantasy.MinigameMapTemplateLoader;
import wily.legacy.fantasy.RuntimeWorldHandle;
import wily.legacy.minigame.Minigame;
import wily.legacy.minigame.grf.GrfMap;
import wily.legacy.minigame.networking.S2CGlideCheckpointPayload;
import wily.legacy.minigame.networking.S2CLeaderboardPayload;
import wily.legacy.minigame.networking.S2CMapTransitionPayload;

import java.util.*;

/**
 * Glide minigame controller: Elytra racing through checkpoints.
 * Players fly through checkpoints to complete a course as fast as possible.
 *
 * Loads the race track from a .mcsave template pack via the Fantasy API,
 * creating a temporary dimension that is deleted when the game ends.
 */
public class GlideMinigameController extends AbstractMinigameController<GlideMinigameController> {

    private static final int GAME_START_COUNTDOWN = 20 * 5;
    private static final int MAX_GAME_TICKS = 20 * 60 * 10;

    private final Map<UUID, PlayerGlideState> playerStates = new HashMap<>();
    private List<BlockPos> checkpoints = new ArrayList<>();
    private GlidePhase phase = GlidePhase.LOADING;
    private int phaseTimer = 0;
    private int finishedCount = 0;

    /** Handle to the template-loaded Fantasy world, used to delete it on game end. */
    private RuntimeWorldHandle worldHandle = null;

    public enum GlidePhase { LOADING, COUNTDOWN, RACING, FINISHED }

    public GlideMinigameController(ServerLevel level) {
        super(level);
    }

    @Override
    protected void onStart() {
        phase = GlidePhase.LOADING;
        phaseTimer = 0;
        finishedCount = 0;
        playerStates.clear();

        ResourceLocation mapId = data.mapId();
        GrfMap grfMap = GrfMap.load(mapId);

        // Determine the template pack to load (GRF can override the default)
        ResourceLocation templateId = grfMap != null ? grfMap.getTemplatePackId() : mapId;

        // Notify clients about the incoming map transition
        broadcastToAllPlayers(new S2CMapTransitionPayload(mapId, Minigame.GLIDE.getName(), 60));

        // Load the .mcsave template world; once loaded, initialize the game
        boolean hasTemplate = MinigameMapTemplateLoader.hasTemplate(level.getServer(), templateId);
        if (hasTemplate) {
            RuntimeWorldHandle handle = MinigameMapTemplateLoader.loadTemplateHandle(level.getServer(), templateId);
            if (handle != null) {
                worldHandle = handle;
                this.level = handle.asWorld();
                Legacy4J.LOGGER.info("[Legacy4J Glide] Loaded template world '{}' for map '{}'", templateId, mapId);
            } else {
                Legacy4J.LOGGER.warn("[Legacy4J Glide] Template load failed for '{}', using current level", templateId);
            }
        } else {
            Legacy4J.LOGGER.info("[Legacy4J Glide] No template pack for '{}', using current level", templateId);
        }

        // Load checkpoints from GRF
        if (grfMap != null) {
            checkpoints = grfMap.getCheckpoints().stream()
                    .map(c -> c.position())
                    .toList();
        } else {
            checkpoints = GrfMap.loadCheckpoints(mapId);
        }

        // Resolve spawn position
        Vec3 spawnVec = (grfMap != null && grfMap.getSpawnPos() != null)
                ? grfMap.getSpawnPos()
                : new Vec3(level.getSharedSpawnPos().getX(), level.getSharedSpawnPos().getY() + 1,
                           level.getSharedSpawnPos().getZ());

        // Set up players
        for (UUID uuid : players) {
            playerStates.put(uuid, new PlayerGlideState(checkpoints.size()));
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
            if (player != null) {
                player.teleportTo(level, spawnVec.x, spawnVec.y, spawnVec.z, player.getYRot(), player.getXRot());
                setupPlayer(player);
            }
        }

        phase = GlidePhase.COUNTDOWN;
        phaseTimer = 0;
    }

    private void setupPlayer(ServerPlayer player) {
        player.setGameMode(GameType.ADVENTURE);
        ItemStack elytra = new ItemStack(Items.ELYTRA);
        player.setItemSlot(EquipmentSlot.CHEST, elytra);
    }

    @Override
    protected void onEnd() {
        playerStates.clear();
        checkpoints = new ArrayList<>();
        if (worldHandle != null) {
            worldHandle.delete();
            worldHandle = null;
            Legacy4J.LOGGER.info("[Legacy4J Glide] Deleted Glide template world.");
        }
    }

    @Override
    protected void onTick() {
        phaseTimer++;
        switch (phase) {
            case LOADING -> {
                // Loading is now synchronous, so this phase is only briefly entered
                if (phaseTimer > 40) {
                    Legacy4J.LOGGER.warn("[Legacy4J Glide] Still in LOADING phase after 2s, forcing COUNTDOWN.");
                    phase = GlidePhase.COUNTDOWN;
                    phaseTimer = 0;
                }
            }
            case COUNTDOWN -> {
                int remaining = GAME_START_COUNTDOWN - phaseTimer;
                if (remaining <= 0) {
                    phase = GlidePhase.RACING;
                    phaseTimer = 0;
                    broadcastTitle(Component.translatable("legacy.minigame.glide.go"),
                            Component.translatable("legacy.minigame.glide.fly_through"));
                    // Set race start time for all players now
                    long now = System.currentTimeMillis();
                    playerStates.values().forEach(s -> {
                        s.startTimeMs = now;
                        s.lastCheckpointTimeMs = now;
                    });
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
            case FINISHED -> {}
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
                        new S2CGlideCheckpointPayload(state.nextCheckpoint, checkpoints.size(),
                                state.lastCheckpointTimeMs - state.startTimeMs));

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
        MinigamesController.addStats(player.getUUID(), Minigame.GLIDE,
                (int)(MAX_GAME_TICKS - (state.finishTimeMs - state.startTimeMs) / 50));

        if (finishedCount >= players.size()) {
            phase = GlidePhase.FINISHED;
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
        long startTimeMs = 0;
        long lastCheckpointTimeMs = 0;
        long finishTimeMs = 0;
        boolean finished = false;

        PlayerGlideState(int totalCheckpoints) {
            this.totalCheckpoints = totalCheckpoints;
        }
    }
}

