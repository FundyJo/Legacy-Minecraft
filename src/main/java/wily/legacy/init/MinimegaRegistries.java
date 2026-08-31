package wily.legacy.init;

public final class MinimegaRegistries {
    private MinimegaRegistries() {
    }

    public static void register() {
        // Phase 2 intentionally does not register synthetic Minimega block/sound IDs.
        // The original registry entries are not recoverable from the current source tree,
        // and fake IDs would violate Minimega's source-of-truth requirement.
    }
}
