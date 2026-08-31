package wily.legacy.minigame.controller;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import wily.legacy.minigame.Minigame;
import wily.legacy.minigame.joindata.CreateOrJoin;
import wily.legacy.minigame.minigamedata.MinigameData;

import java.util.List;

public abstract class AbstractMinigameController {
    protected final MinigamesController controller;
    private MinigameData minigameData = new MinigameData(List.of(), Minigame.NONE, 1, wily.legacy.minigame.config.NoConfig.DEFAULT, false, false);

    protected AbstractMinigameController(MinigamesController controller) {
        this.controller = controller;
    }

    public void tick(boolean frozen) {
        tick();
    }

    protected void tick() {
    }

    public void playerLoadedIn(ServerPlayer player) {
    }

    public void playerReady(ServerPlayer player, boolean ready) {
    }

    public void playerVoted(ServerPlayer player, Identifier resourceLocation) {
    }

    public void playerRestart(ServerPlayer player, boolean fromStart) {
    }

    public void playerTimerSynchronization(ServerPlayer player, int number) {
    }

    public void playerTakeAll(ServerPlayer player) {
    }

    public void playerJoiningChoice(ServerPlayer player, CreateOrJoin data) {
    }

    public void playerRecreation(ServerPlayer player, MinigameData data) {
    }

    public MinigameData getMinigameData() {
        return minigameData;
    }

    public void acceptMinigameData(MinigameData data) {
        this.minigameData = data;
    }

    public boolean canAcceptNewPlayers() {
        return true;
    }

    public boolean takeAllEnabled() {
        return false;
    }

    public static final class NoneMinigameController extends AbstractMinigameController {
        public NoneMinigameController(MinigamesController controller) {
            super(controller);
        }
    }
}
