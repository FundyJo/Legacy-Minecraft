package wily.legacy.fantasy;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wily.legacy.Legacy4J;
import wily.legacy.fantasy.util.VoidChunkGenerator;
import wily.legacy.mixin.base.fantasy.MinecraftServerAccess;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Loads .mcsave template pack files as temporary Fantasy dimensions for minigame maps.
 *
 * Template packs (.mcsave files) are ZIP archives containing Minecraft world data
 * (region files, entity data, POI data, etc.). This class:
 * 1. Finds the .mcsave file (from classpath resources or server directory)
 * 2. Extracts it to the Fantasy dimension storage path
 * 3. Creates a temporary Fantasy world that reads the extracted data
 * 4. Calls back with the created ServerLevel
 *
 * Template files are looked up in two places (in order):
 *   1. Classpath: /assets/legacy/minigame/templates/{mapId.path}.mcsave
 *   2. Server dir: world_templates/minigame/{mapId.path}.mcsave
 */
public final class MinigameMapTemplateLoader {

    private static final Logger LOGGER = LogManager.getLogger(MinigameMapTemplateLoader.class);
    private static final String CLASSPATH_PREFIX = "/assets/legacy/minigame/templates/";
    private static final String SERVER_DIR_PREFIX = "world_templates/minigame/";

    private MinigameMapTemplateLoader() {}

    /**
     * Loads a minigame map template and creates a temporary Fantasy world.
     *
     * @param server    The current Minecraft server
     * @param mapId     The resource location of the map (e.g. legacy:glide/cavern_normal)
     * @param onLoaded  Called with the created ServerLevel when ready; may be called on next tick
     * @param onFailed  Called if no template is found or loading fails (may be null)
     */
    public static void loadTemplate(MinecraftServer server, ResourceLocation mapId,
                                    Consumer<ServerLevel> onLoaded, Runnable onFailed) {
        InputStream stream = findTemplateStream(server, mapId);
        if (stream == null) {
            LOGGER.warn("[MinigameMapTemplateLoader] No .mcsave template found for map '{}'. " +
                    "Place it at classpath:{}{}.mcsave or server:{}{}.mcsave",
                    mapId, CLASSPATH_PREFIX, mapId.getPath(), SERVER_DIR_PREFIX, mapId.getPath());
            if (onFailed != null) onFailed.run();
            return;
        }

        // Generate a unique dimension key so concurrent games don't collide
        String uid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String dimPath = "minigame/" + mapId.getPath().replace("/", "_") + "_" + uid;
        ResourceLocation dimId = Legacy4J.createModLocation(dimPath);
        ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION, dimId);

        // Resolve where the dimension data should be stored
        MinecraftServerAccess serverAccess = (MinecraftServerAccess) server;
        LevelStorageSource.LevelStorageAccess storageAccess = serverAccess.getStorageSource();
        Path dimensionDir = storageAccess.getDimensionPath(worldKey);

        // Extract the .mcsave ZIP to the dimension directory
        try {
            Legacy4J.copySaveToDirectory(stream, dimensionDir.toFile());
            LOGGER.info("[MinigameMapTemplateLoader] Extracted template '{}' to '{}'", mapId, dimensionDir);
        } catch (Exception e) {
            LOGGER.error("[MinigameMapTemplateLoader] Failed to extract template '{}': {}", mapId, e.getMessage());
            if (onFailed != null) onFailed.run();
            return;
        }

        // Build a temporary Fantasy world config using a VoidChunkGenerator
        // (ungenerated chunks outside the template will be empty/void)
        RuntimeWorldConfig config = new RuntimeWorldConfig()
                .setGenerator(new VoidChunkGenerator(server))
                .setShouldTickTime(false)
                .setMirrorOverworldGameRules(false);

        try {
            Fantasy fantasy = Fantasy.get(server);
            RuntimeWorldHandle handle = fantasy.openTemporaryWorld(dimId, config);
            ServerLevel level = handle.asWorld();
            LOGGER.info("[MinigameMapTemplateLoader] Created temporary world '{}' for map '{}'",
                    dimId, mapId);
            if (onLoaded != null) onLoaded.accept(level);
        } catch (Exception e) {
            LOGGER.error("[MinigameMapTemplateLoader] Failed to create Fantasy world for '{}': {}",
                    mapId, e.getMessage());
            // Attempt to clean up extracted data
            try { FileUtils.deleteDirectory(dimensionDir.toFile()); } catch (IOException ignored) {}
            if (onFailed != null) onFailed.run();
        }
    }

    /**
     * Synchronously creates a template world and returns a handle.
     * Returns null if no template is found or creation fails.
     */
    public static RuntimeWorldHandle loadTemplateHandle(MinecraftServer server, ResourceLocation mapId) {
        InputStream stream = findTemplateStream(server, mapId);
        if (stream == null) {
            LOGGER.warn("[MinigameMapTemplateLoader] No .mcsave template found for map '{}'", mapId);
            return null;
        }

        String uid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String dimPath = "minigame/" + mapId.getPath().replace("/", "_") + "_" + uid;
        ResourceLocation dimId = Legacy4J.createModLocation(dimPath);
        ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION, dimId);

        MinecraftServerAccess serverAccess = (MinecraftServerAccess) server;
        LevelStorageSource.LevelStorageAccess storageAccess = serverAccess.getStorageSource();
        Path dimensionDir = storageAccess.getDimensionPath(worldKey);

        try {
            Legacy4J.copySaveToDirectory(stream, dimensionDir.toFile());
        } catch (Exception e) {
            LOGGER.error("[MinigameMapTemplateLoader] Failed to extract template '{}': {}", mapId, e.getMessage());
            return null;
        }

        RuntimeWorldConfig config = new RuntimeWorldConfig()
                .setGenerator(new VoidChunkGenerator(server))
                .setShouldTickTime(false)
                .setMirrorOverworldGameRules(false);

        try {
            Fantasy fantasy = Fantasy.get(server);
            RuntimeWorldHandle handle = fantasy.openTemporaryWorld(dimId, config);
            LOGGER.info("[MinigameMapTemplateLoader] Created template world '{}' for map '{}'", dimId, mapId);
            return handle;
        } catch (Exception e) {
            LOGGER.error("[MinigameMapTemplateLoader] Failed to open template world for '{}': {}", mapId, e.getMessage());
            try { FileUtils.deleteDirectory(dimensionDir.toFile()); } catch (IOException ignored) {}
            return null;
        }
    }

    /**
     * Checks whether a template pack exists for the given map.
     * Does not open a stream; uses existence checks only.
     */
    public static boolean hasTemplate(MinecraftServer server, ResourceLocation mapId) {
        // Check classpath resource
        String classPath = CLASSPATH_PREFIX + mapId.getPath() + ".mcsave";
        if (MinigameMapTemplateLoader.class.getResource(classPath) != null) {
            return true;
        }
        // Check server directory
        Path serverPath = server.getServerDirectory().resolve(SERVER_DIR_PREFIX + mapId.getPath() + ".mcsave");
        return Files.exists(serverPath);
    }

    private static InputStream findTemplateStream(MinecraftServer server, ResourceLocation mapId) {
        // 1. Classpath (bundled in the mod JAR or resource packs)
        String classPath = CLASSPATH_PREFIX + mapId.getPath() + ".mcsave";
        InputStream stream = MinigameMapTemplateLoader.class.getResourceAsStream(classPath);
        if (stream != null) {
            LOGGER.debug("[MinigameMapTemplateLoader] Found template at classpath '{}'", classPath);
            return stream;
        }

        // 2. Server directory (operator-provided templates)
        Path serverPath = server.getServerDirectory().resolve(SERVER_DIR_PREFIX + mapId.getPath() + ".mcsave");
        if (Files.exists(serverPath)) {
            try {
                LOGGER.debug("[MinigameMapTemplateLoader] Found template at server path '{}'", serverPath);
                return Files.newInputStream(serverPath);
            } catch (IOException e) {
                LOGGER.warn("[MinigameMapTemplateLoader] Could not open template '{}': {}", serverPath, e.getMessage());
            }
        }

        return null;
    }
}
