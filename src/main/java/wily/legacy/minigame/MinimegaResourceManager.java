package wily.legacy.minigame;

import net.minecraft.server.players.PlayerList;

import java.util.List;

public final class MinimegaResourceManager {
    public static final String NAMESPACE = "minimega";
    public static final List<String> RESOURCE_DIRECTORIES = List.of(
            "assets/minimega",
            "data/minimega"
    );

    private MinimegaResourceManager() {
    }

    public static void init() {
        // Phase 2 intentionally starts with loader-neutral namespace wiring only.
        // Full map metadata, resource pack variant handling, and upstream asset parity
        // remain blocked until the original Minimega source is available.
    }

    public static void onResourcesReload(PlayerList playerList) {
        // Keep the common resource lifecycle neutral and avoid direct Fabric-only
        // ResourceReloadListener abstractions. Legacy4J's existing factory event flow
        // is used instead.
    }
}
