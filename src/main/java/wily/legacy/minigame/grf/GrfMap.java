package wily.legacy.minigame.grf;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import wily.legacy.Legacy4J;
import wily.legacy.minigame.grf.element.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;

/**
 * GRF (Game Rules Format) map loader and parser.
 * Parses XML-based map definition files for minigames.
 *
 * The GRF file provides game-logic metadata (checkpoints, spawn positions, rules)
 * while the actual world terrain is loaded from a .mcsave template pack.
 */
public class GrfMap {

    public static final String GRF_PATH_PREFIX = "legacy/minigame/maps/";

    private final ResourceLocation id;
    private final LevelRules levelRules;
    private final List<Checkpoint> checkpoints;
    private final List<FistfightFlag> fistfightFlags;
    private final ForcedBiome forcedBiome;
    private final List<UpdatePlayer> updatePlayers;
    /** Path of the .mcsave template pack relative to the map root (without extension). */
    private final String templatePackPath;
    /** Default spawn position for this map (from the GRF <SpawnPos> element). */
    private final Vec3 spawnPos;

    public GrfMap(ResourceLocation id, LevelRules levelRules, List<Checkpoint> checkpoints,
                  List<FistfightFlag> fistfightFlags, ForcedBiome forcedBiome,
                  List<UpdatePlayer> updatePlayers, String templatePackPath, Vec3 spawnPos) {
        this.id = id;
        this.levelRules = levelRules;
        this.checkpoints = Collections.unmodifiableList(checkpoints);
        this.fistfightFlags = Collections.unmodifiableList(fistfightFlags);
        this.forcedBiome = forcedBiome;
        this.updatePlayers = Collections.unmodifiableList(updatePlayers);
        this.templatePackPath = templatePackPath;
        this.spawnPos = spawnPos;
    }

    public ResourceLocation getId() { return id; }
    public LevelRules getLevelRules() { return levelRules; }
    public List<Checkpoint> getCheckpoints() { return checkpoints; }
    public List<FistfightFlag> getFistfightFlags() { return fistfightFlags; }
    public ForcedBiome getForcedBiome() { return forcedBiome; }
    public List<UpdatePlayer> getUpdatePlayers() { return updatePlayers; }

    /**
     * Returns the .mcsave template pack path for this map.
     * The ResourceLocation for loading is built as: {@code <mapId.namespace>:<templatePackPath>}
     * or, if templatePackPath is empty, defaults to the mapId path itself.
     */
    public String getTemplatePackPath() { return templatePackPath; }

    /**
     * Returns the ResourceLocation to use when looking up the .mcsave template.
     */
    public ResourceLocation getTemplatePackId() {
        if (templatePackPath != null && !templatePackPath.isEmpty()) {
            return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), templatePackPath);
        }
        return id;
    }

    /** Returns the default player spawn position for this map (may be null). */
    public Vec3 getSpawnPos() { return spawnPos; }

    /**
     * Load checkpoints for a given map from GRF data.
     * Returns an empty list if the map has no checkpoint data.
     */
    public static List<BlockPos> loadCheckpoints(ResourceLocation mapId) {
        try {
            GrfMap map = load(mapId);
            if (map != null) {
                return map.getCheckpoints().stream()
                        .map(Checkpoint::position)
                        .toList();
            }
        } catch (Exception e) {
            Legacy4J.LOGGER.warn("[Legacy4J GRF] Failed to load checkpoints for {}: {}", mapId, e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * Load and parse a GRF map from the resource pack.
     * The map file is expected at: /assets/{namespace}/minigame/maps/{path}.grf
     */
    public static GrfMap load(ResourceLocation mapId) {
        try {
            String resourcePath = "/assets/" + mapId.getNamespace() + "/minigame/maps/" + mapId.getPath() + ".grf";
            InputStream stream = GrfMap.class.getResourceAsStream(resourcePath);
            if (stream == null) {
                Legacy4J.LOGGER.debug("[Legacy4J GRF] Map not found at {}: {}", resourcePath, mapId);
                return null;
            }
            return parse(mapId, stream);
        } catch (Exception e) {
            Legacy4J.LOGGER.error("[Legacy4J GRF] Error loading map {}: {}", mapId, e.getMessage());
            return null;
        }
    }

    public static GrfMap parse(ResourceLocation id, InputStream stream) throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        var builder = factory.newDocumentBuilder();
        var doc = builder.parse(stream);
        doc.getDocumentElement().normalize();

        var root = doc.getDocumentElement();
        LevelRules levelRules = new LevelRules();
        List<Checkpoint> checkpoints = new ArrayList<>();
        List<FistfightFlag> fistfightFlags = new ArrayList<>();
        List<UpdatePlayer> updatePlayers = new ArrayList<>();
        ForcedBiome forcedBiome = null;
        String templatePackPath = "";
        Vec3 spawnPos = null;

        var nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            var node = nodes.item(i);
            if (node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) continue;
            var element = (org.w3c.dom.Element) node;
            switch (element.getTagName()) {
                case "LevelRules" -> levelRules = LevelRules.parse(element);
                case "Checkpoint" -> checkpoints.add(Checkpoint.parse(element));
                case "FistfightFlag" -> fistfightFlags.add(FistfightFlag.parse(element));
                case "ForcedBiome" -> forcedBiome = ForcedBiome.parse(element);
                case "UpdatePlayer" -> updatePlayers.add(UpdatePlayer.parse(element));
                case "TemplatePack" -> templatePackPath = element.getAttribute("path");
                case "SpawnPos" -> spawnPos = parseVec3(element);
            }
        }
        return new GrfMap(id, levelRules, checkpoints, fistfightFlags, forcedBiome,
                updatePlayers, templatePackPath, spawnPos);
    }

    private static Vec3 parseVec3(org.w3c.dom.Element element) {
        try {
            double x = Double.parseDouble(element.getAttribute("x"));
            double y = Double.parseDouble(element.getAttribute("y"));
            double z = Double.parseDouble(element.getAttribute("z"));
            return new Vec3(x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Discover all available maps for a given minigame type from resources.
     */
    public static List<ResourceLocation> discoverMaps(String minigameName) {
        List<ResourceLocation> maps = new ArrayList<>();
        String[] knownMaps = getKnownMaps(minigameName);
        for (String map : knownMaps) {
            maps.add(Legacy4J.createModLocation(map));
        }
        return maps;
    }

    private static String[] getKnownMaps(String minigameName) {
        return switch (minigameName) {
            case "glide" -> new String[]{"glide/bedrock_box", "glide/crimson_fortress", "glide/skylines",
                    "glide/trial_chamber", "glide/supernova_laboratory", "glide/undersea_oasis"};
            case "fistfight" -> new String[]{"fistfight/skywars", "fistfight/basic_arena"};
            case "tumble" -> new String[]{"tumble/basic_arena"};
            case "lobby" -> new String[]{"lobby/default"};
            default -> new String[]{};
        };
    }
}
