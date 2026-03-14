package wily.legacy.fantasy.util;

/**
 * A three-state boolean that can be TRUE, FALSE, or DEFAULT.
 * Replacement for Fabric's TriState to maintain platform independence.
 */
public enum TriState {
    TRUE,
    FALSE,
    DEFAULT;

    public boolean orElse(boolean fallback) {
        return this == DEFAULT ? fallback : this == TRUE;
    }

    public static TriState of(boolean value) {
        return value ? TRUE : FALSE;
    }
}
