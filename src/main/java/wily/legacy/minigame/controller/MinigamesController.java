package wily.legacy.minigame.controller;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import wily.legacy.minigame.Minigame;
import wily.legacy.minigame.joindata.CreateOrJoin;
import wily.legacy.minigame.minigamedata.MinigameData;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class MinigamesController {
    private static final Map<Level, MinigamesController> BY_LEVEL = new WeakHashMap<>();

    private final Level level;
    private Minigame activeMinigame = Minigame.NONE;
    private AbstractMinigameController minigameController;

    private MinigamesController(Level level) {
        this.level = level;
        this.minigameController = new AbstractMinigameController.NoneMinigameController(this);
    }

    public static MinigamesController getMinigameController(Level level) {
        return BY_LEVEL.computeIfAbsent(level, MinigamesController::new);
    }

    public static void onServerStarted(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            getMinigameController(level);
        }
    }

    public static void onServerStopped(MinecraftServer server) {
        BY_LEVEL.keySet().removeIf(level -> level.getServer() == server);
    }

    public static void onServerTick(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            MinigamesController controller = getMinigameController(level);
            controller.minigameController.tick(level.tickRateManager().isFrozen());
        }
    }

    public static void onPlayerJoin(ServerPlayer player) {
        getMinigameController(player.serverLevel()).playerLoadedIn(player);
    }

    public static void onPlayerLeave(ServerPlayer player) {
        getMinigameController(player.serverLevel()).playerRemoved(player);
    }

    public Minigame getActiveMinigame() {
        return activeMinigame;
    }

    public AbstractMinigameController getCurrentController() {
        return minigameController;
    }

    public boolean isClient() {
        return level.isClientSide();
    }

    public ServerLevel getLevel() {
        return (ServerLevel) level;
    }

    public void setActiveMinigame(Minigame minigame, AbstractMinigameController controller) {
        this.activeMinigame = Objects.requireNonNull(minigame, "minigame");
        this.minigameController = Objects.requireNonNull(controller, "controller");
    }

    public void playerLoadedIn(ServerPlayer player) {
        minigameController.playerLoadedIn(player);
    }

    public void playerRemoved(ServerPlayer player) {
        minigameController.playerRemoved(player);
    }

    public void playerReady(ServerPlayer player, boolean ready) {
        minigameController.playerReady(player, ready);
    }

    public void playerVoted(ServerPlayer player, net.minecraft.resources.Identifier resourceLocation) {
        minigameController.playerVoted(player, resourceLocation);
    }

    public void playerRestart(ServerPlayer player, boolean fromStart) {
        minigameController.playerRestart(player, fromStart);
    }

    public void playerTimerSynchronization(ServerPlayer player, int number) {
        minigameController.playerTimerSynchronization(player, number);
    }

    public void playerTakeAll(ServerPlayer player) {
        minigameController.playerTakeAll(player);
    }

    public void playerJoiningChoice(ServerPlayer player, CreateOrJoin data) {
        minigameController.playerJoiningChoice(player, data);
    }

    public void playerRecreation(ServerPlayer player, MinigameData data) {
        minigameController.playerRecreation(player, data);
    }
}
