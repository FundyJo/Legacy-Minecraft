package wily.legacy.minigame.grf.element;

import net.minecraft.core.BlockPos;
import org.w3c.dom.Element;

/**
 * GRF element: FistfightFlag - spawn point or scoring zone for Fistfight maps.
 */
public record FistfightFlag(int id, BlockPos position, FlagType type) implements GrfElement {

    public enum FlagType { SPAWN, CAPTURE_ZONE, DEATH_ZONE }

    public static FistfightFlag parse(Element element) {
        int id = parseInt(element, "id", 0);
        int x = parseInt(element, "x", 0);
        int y = parseInt(element, "y", 64);
        int z = parseInt(element, "z", 0);
        String typeStr = element.getAttribute("type");
        FlagType type = switch (typeStr) {
            case "capture_zone" -> FlagType.CAPTURE_ZONE;
            case "death_zone" -> FlagType.DEATH_ZONE;
            default -> FlagType.SPAWN;
        };
        return new FistfightFlag(id, new BlockPos(x, y, z), type);
    }

    private static int parseInt(Element e, String attr, int def) {
        String val = e.getAttribute(attr);
        try { return val.isEmpty() ? def : Integer.parseInt(val); }
        catch (NumberFormatException ex) { return def; }
    }
}
