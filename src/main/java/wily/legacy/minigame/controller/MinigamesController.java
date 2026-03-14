package wily.legacy.minigame.controller;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import wily.factoryapi.base.config.FactoryConfig;
import wily.legacy.Legacy4J;
import wily.legacy.minigame.Minigame;
import wily.legacy.minigame.MinigameData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Per-server minigame controller manager.
 * Uses FactoryConfig for persistent player statistics (instead of Fabric AttachmentType).
 * Active controllers are kept in memory; only stats are persisted via FactoryConfig.
 */
public class MinigamesController {

    public static final FactoryConfig.StorageHandler MINIGAME_STATS_STORAGE = new FactoryConfig.StorageHandler();
    public static final FactoryConfig<List<PlayerMinigameStats>> playerStats = MINIGAME_STATS_STORAGE.register(
            FactoryConfig.create("playerStats", null, () -> PlayerMinigameStats.LIST_CODEC,
                    new ArrayList<>(), v -> {}, MINIGAME_STATS_STORAGE)
    );

    private static final Map<ResourceLocation, AbstractMinigameController<?>> ACTIVE_CONTROLLERS = new ConcurrentHashMap<>();
    private static MinecraftServer currentServer;

    public static void onServerStart(MinecraftServer server) {
        currentServer = server;
        ACTIVE_CONTROLLERS.clear();
        MINIGAME_STATS_STORAGE.withServerFile(server, "legacy/minigame_stats.json").resetAndLoad();
        Legacy4J.LOGGER.info("[Legacy4J Minigames] Minigame system started.");
    }
    public static void onServerStop(MinecraftServer server) {
        ACTIVE_CONTROLLERS.values().forEach(c -> { if (c.isActive()) c.end(); });
        ACTIVE_CONTROLLERS.clear();
        currentServer = null;
    }

    public static AbstractMinigameController<?> getController(ServerLevel level) {
        return ACTIVE_CONTROLLERS.get(level.dimension().location());
    }

    public static boolean hasActiveGame(ServerLevel level) {
        AbstractMinigameController<?> c = getController(level);
        return c != null && c.isActive();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void startMinigame(ServerLevel level, Minigame<?> minigame, MinigameData data) {
        ResourceLocation dimKey = level.dimension().location();
        AbstractMinigameController<?> existing = ACTIVE_CONTROLLERS.get(dimKey);
        if (existing != null && existing.isActive()) {
            existing.end();
        }
        AbstractMinigameController<?> controller = ((Minigame) minigame).newController(level);
        controller.start(data);
        ACTIVE_CONTROLLERS.put(dimKey, controller);
        Legacy4J.LOGGER.info("[Legacy4J Minigames] Started {} in {}", minigame.getName(), dimKey);
    }

    public static void endMinigame(ServerLevel level) {
        AbstractMinigameController<?> c = ACTIVE_CONTROLLERS.remove(level.dimension().location());
        if (c != null && c.isActive()) c.end();
    }

    public static void tickAll(MinecraftServer server) {
        for (Map.Entry<ResourceLocation, AbstractMinigameController<?>> entry : ACTIVE_CONTROLLERS.entrySet()) {
            ServerLevel lvl = server.getLevel(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, entry.getKey()));
            if (lvl != null) entry.getValue().tick(lvl);
        }
    }

    public static void addStats(UUID playerId, Minigame<?> minigame, int score) {
        List<PlayerMinigameStats> stats = playerStats.get();
        boolean found = false;
        for (PlayerMinigameStats s : stats) {
            if (s.playerId().equals(playerId) && s.minigameName().equals(minigame.getName())) {
                s.addScore(score);
                found = true;
                break;
            }
        }
        if (!found) {
            stats.add(new PlayerMinigameStats(playerId, minigame.getName(), score, 0, 1));
        }
        MINIGAME_STATS_STORAGE.markDirty();
    }

    public static MinecraftServer getCurrentServer() { return currentServer; }

    public static Map<ResourceLocation, AbstractMinigameController<?>> getActiveControllers() {
        return Collections.unmodifiableMap(ACTIVE_CONTROLLERS);
    }

    public static class PlayerMinigameStats {
        public static final Codec<PlayerMinigameStats> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("playerId").forGetter(s -> s.playerId),
                Codec.STRING.fieldOf("minigame").forGetter(s -> s.minigameName),
                Codec.INT.fieldOf("score").forGetter(s -> s.score),
                Codec.INT.fieldOf("wins").forGetter(s -> s.wins),
                Codec.INT.fieldOf("gamesPlayed").forGetter(s -> s.gamesPlayed)
        ).apply(i, PlayerMinigameStats::new));
        public static final Codec<List<PlayerMinigameStats>> LIST_CODEC = CODEC.listOf()
                .xmap(ArrayList::new, Function.identity());

        private final UUID playerId;
        private final String minigameName;
        private int score;
        private int wins;
        private int gamesPlayed;

        public PlayerMinigameStats(UUID playerId, String minigameName, int score, int wins, int gamesPlayed) {
            this.playerId = playerId;
            this.minigameName = minigameName;
            this.score = score;
            this.wins = wins;
            this.gamesPlayed = gamesPlayed;
        }

        public UUID playerId() { return playerId; }
        public String minigameName() { return minigameName; }
        public int score() { return score; }
        public int wins() { return wins; }
        public int gamesPlayed() { return gamesPlayed; }

        public void addScore(int s) { this.score += s; this.gamesPlayed++; }
        public void addWin() { this.wins++; }
    }
}
