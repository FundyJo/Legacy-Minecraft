package wily.legacy.minigame.grf.element;

import org.w3c.dom.Element;

/**
 * GRF element: LevelRules - defines overall game rules for the map.
 */
public class LevelRules implements GrfElement {
    private boolean pvp = true;
    private boolean naturalRegeneration = false;
    private boolean keepInventory = true;
    private boolean showDeathMessages = true;
    private boolean friendlyFire = true;
    private int timeOfDay = 6000;
    private boolean weather = false;

    public LevelRules() {}

    public LevelRules(boolean pvp, boolean naturalRegeneration, boolean keepInventory,
                      boolean showDeathMessages, boolean friendlyFire, int timeOfDay, boolean weather) {
        this.pvp = pvp;
        this.naturalRegeneration = naturalRegeneration;
        this.keepInventory = keepInventory;
        this.showDeathMessages = showDeathMessages;
        this.friendlyFire = friendlyFire;
        this.timeOfDay = timeOfDay;
        this.weather = weather;
    }

    public static LevelRules parse(Element element) {
        LevelRules rules = new LevelRules();
        rules.pvp = parseBool(element, "pvp", true);
        rules.naturalRegeneration = parseBool(element, "naturalRegeneration", false);
        rules.keepInventory = parseBool(element, "keepInventory", true);
        rules.showDeathMessages = parseBool(element, "showDeathMessages", true);
        rules.friendlyFire = parseBool(element, "friendlyFire", true);
        rules.timeOfDay = parseInt(element, "timeOfDay", 6000);
        rules.weather = parseBool(element, "weather", false);
        return rules;
    }

    private static boolean parseBool(Element e, String attr, boolean def) {
        String val = e.getAttribute(attr);
        return val.isEmpty() ? def : Boolean.parseBoolean(val);
    }

    private static int parseInt(Element e, String attr, int def) {
        String val = e.getAttribute(attr);
        try { return val.isEmpty() ? def : Integer.parseInt(val); }
        catch (NumberFormatException ex) { return def; }
    }

    public boolean isPvp() { return pvp; }
    public boolean isNaturalRegeneration() { return naturalRegeneration; }
    public boolean isKeepInventory() { return keepInventory; }
    public boolean isShowDeathMessages() { return showDeathMessages; }
    public boolean isFriendlyFire() { return friendlyFire; }
    public int getTimeOfDay() { return timeOfDay; }
    public boolean isWeather() { return weather; }
}
