package wily.legacy.minigame.grf.element;

import net.minecraft.core.BlockPos;
import org.w3c.dom.Element;

/**
 * GRF element: Checkpoint - a position the player must pass through.
 * Used for Glide minigame course definition.
 */
public record Checkpoint(int index, BlockPos position, int radius) implements GrfElement {

    public static Checkpoint parse(Element element) {
        int index = parseInt(element, "index", 0);
        int x = parseInt(element, "x", 0);
        int y = parseInt(element, "y", 64);
        int z = parseInt(element, "z", 0);
        int radius = parseInt(element, "radius", 5);
        return new Checkpoint(index, new BlockPos(x, y, z), radius);
    }

    private static int parseInt(Element e, String attr, int def) {
        String val = e.getAttribute(attr);
        try { return val.isEmpty() ? def : Integer.parseInt(val); }
        catch (NumberFormatException ex) { return def; }
    }

    public boolean contains(net.minecraft.world.phys.Vec3 pos) {
        return new net.minecraft.world.phys.AABB(position).inflate(radius).contains(pos);
    }
}
