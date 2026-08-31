package wily.legacy.minigame;

import net.minecraft.server.players.PlayerList;

public final class MinimegaResourceManager {
    private MinimegaResourceManager() {
    }

    public static void init() {
        // Resource recovery is currently blocked because no verified upstream Minimega
        // asset tree was found in the available sources. Keep this class inert and do
        // not register fake resource directories while source parity remains unresolved.
    }

    public static void onResourcesReload(PlayerList playerList) {
        // No-op: Legacy4J should not initialize or reload synthetic Minimega resources
        // before the original assets are recovered and verified.
    }
}
