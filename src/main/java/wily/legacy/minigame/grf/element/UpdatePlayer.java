package wily.legacy.minigame.grf.element;

import org.w3c.dom.Element;

/**
 * GRF element: UpdatePlayer - defines player state changes on game events.
 */
public record UpdatePlayer(String trigger, float health, int foodLevel, boolean clearInventory) implements GrfElement {

    public static UpdatePlayer parse(Element element) {
        String trigger = element.getAttribute("trigger");
        if (trigger.isEmpty()) trigger = "spawn";
        float health = parseFloat(element, "health", 20.0f);
        int foodLevel = parseInt(element, "foodLevel", 20);
        boolean clearInventory = Boolean.parseBoolean(element.getAttribute("clearInventory"));
        return new UpdatePlayer(trigger, health, foodLevel, clearInventory);
    }

    private static float parseFloat(Element e, String attr, float def) {
        String val = e.getAttribute(attr);
        try { return val.isEmpty() ? def : Float.parseFloat(val); }
        catch (NumberFormatException ex) { return def; }
    }

    private static int parseInt(Element e, String attr, int def) {
        String val = e.getAttribute(attr);
        try { return val.isEmpty() ? def : Integer.parseInt(val); }
        catch (NumberFormatException ex) { return def; }
    }
}
