package wily.legacy.minigame.controller;

import net.minecraft.server.level.ServerLevel;
import wily.legacy.minigame.Minigame;

/**
 * No-op controller for when no minigame is active.
 */
public class NoneMinigameController extends AbstractMinigameController<NoneMinigameController> {

    public NoneMinigameController(ServerLevel level) {
        super(level);
    }

    @Override
    protected void onStart() {}

    @Override
    protected void onEnd() {}

    @Override
    protected void onTick() {}

    @Override
    public Minigame<NoneMinigameController> getMinigame() {
        return Minigame.NONE;
    }
}
