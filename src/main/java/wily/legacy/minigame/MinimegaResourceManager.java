package wily.legacy.minigame;

import net.minecraft.server.players.PlayerList;

public final class MinimegaResourceManager {
    private MinimegaResourceManager() {
    }

    public static void init() {
        // Phase-2 runtime wiring remains intentionally inert until FundyJo/Minimega-backed
        // loader parity is implemented in Legacy4J. Resources may exist on disk, but no
        // synthetic loader behavior should be introduced here.
    }

    public static void onResourcesReload(PlayerList playerList) {
        // No-op by design until FundyJo/Minimega loader parity is implemented.
    }
}
