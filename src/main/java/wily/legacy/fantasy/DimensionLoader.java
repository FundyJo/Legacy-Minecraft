package wily.legacy.fantasy;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wily.factoryapi.FactoryEvent;
import wily.legacy.mixin.base.fantasy.MinecraftServerAccess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Utility-Klasse zum dynamischen Laden und Entladen von Dimensionen.
 * Ermöglicht das Erstellen temporärer Welten basierend auf vorhandenen Dimensionen.
 */
public class DimensionLoader {
    private static final Logger LOGGER = LogManager.getLogger(DimensionLoader.class);
    private static final HashMap<ResourceLocation, CustomDimensionHolder> loadedWorlds = new HashMap<>();

    static {
        // Automatisches Tick-System für das Laden/Entladen von Welten
        FactoryEvent.preServerTick(DimensionLoader::tick);
    }

    /**
     * Lädt eine Dimension aus einer vorhandenen Welt.
     *
     * @param server Der Minecraft-Server
     * @param dimensionId Die ID der neuen Dimension
     * @param copyFromId Die ID der zu kopierenden Dimension
     * @param onComplete Callback, der nach dem Laden ausgeführt wird
     */
    public static void loadDimension(MinecraftServer server, ResourceLocation dimensionId, ResourceLocation copyFromId, BiConsumer<MinecraftServer, CustomDimensionHolder> onComplete) {
        if (loadedWorlds.containsKey(dimensionId)) {
            LOGGER.warn("Dimension {} ist bereits geladen", dimensionId);
            return;
        }

        CustomDimensionHolder holder = new CustomDimensionHolder(dimensionId, copyFromId);
        if (onComplete != null) {
            holder.setCompleteTask(onComplete);
        }

        loadedWorlds.put(dimensionId, holder);
        LOGGER.info("Dimension {} wird zum Laden vorbereitet", dimensionId);
    }

    /**
     * Lädt eine Dimension ohne Callback.
     */
    public static void loadDimension(MinecraftServer server, ResourceLocation dimensionId, ResourceLocation copyFromId) {
        loadDimension(server, dimensionId, copyFromId, null);
    }

    /**
     * Lädt eine externe Welt als Dimension.
     *
     * @param server Der Minecraft-Server
     * @param dimensionId Die ID der neuen Dimension
     * @param externalWorldPath Pfad zur externen Welt
     * @param onComplete Callback, der nach dem Laden ausgeführt wird
     */
    public static void loadExternalWorld(MinecraftServer server, ResourceLocation dimensionId, Path externalWorldPath, BiConsumer<MinecraftServer, CustomDimensionHolder> onComplete) {
        if (loadedWorlds.containsKey(dimensionId)) {
            LOGGER.warn("Dimension {} ist bereits geladen", dimensionId);
            return;
        }

        // Verwende einen Platzhalter für copyFromId (wird nicht verwendet für externe Welten)
        CustomDimensionHolder holder = new CustomDimensionHolder(dimensionId, dimensionId);
        holder.externalWorldPath = externalWorldPath;

        if (onComplete != null) {
            holder.setCompleteTask(onComplete);
        }

        loadedWorlds.put(dimensionId, holder);
        LOGGER.info("Externe Welt {} wird zum Laden vorbereitet von: {}", dimensionId, externalWorldPath);
    }

    /**
     * Lädt eine externe Welt ohne Callback.
     */
    public static void loadExternalWorld(MinecraftServer server, ResourceLocation dimensionId, Path externalWorldPath) {
        loadExternalWorld(server, dimensionId, externalWorldPath, null);
    }

    /**
     * Lädt eine externe Welt als Dimension (String-Pfad-Version).
     */
    public static void loadExternalWorld(MinecraftServer server, ResourceLocation dimensionId, String externalWorldPath, BiConsumer<MinecraftServer, CustomDimensionHolder> onComplete) {
        loadExternalWorld(server, dimensionId, Path.of(externalWorldPath), onComplete);
    }

    /**
     * Lädt eine externe Welt ohne Callback (String-Pfad-Version).
     */
    public static void loadExternalWorld(MinecraftServer server, ResourceLocation dimensionId, String externalWorldPath) {
        loadExternalWorld(server, dimensionId, Path.of(externalWorldPath), null);
    }

    /**
     * Entlädt eine geladene Dimension.
     *
     * @param dimensionId Die ID der zu entladenden Dimension
     * @param onComplete Callback, der nach dem Entladen ausgeführt wird
     */
    public static void unloadDimension(ResourceLocation dimensionId, BiConsumer<MinecraftServer, CustomDimensionHolder> onComplete) {
        CustomDimensionHolder holder = loadedWorlds.get(dimensionId);
        if (holder == null) {
            LOGGER.warn("Dimension {} nicht gefunden", dimensionId);
            return;
        }

        if (onComplete != null) {
            holder.setCompleteTask(onComplete);
        }
        holder.scheduleToDelete();
        LOGGER.info("Dimension {} wird zum Entladen markiert", dimensionId);
    }

    /**
     * Entlädt eine Dimension ohne Callback.
     */
    public static void unloadDimension(ResourceLocation dimensionId) {
        unloadDimension(dimensionId, null);
    }

    /**
     * Prüft, ob eine Dimension geladen ist.
     */
    public static boolean isDimensionLoaded(ResourceLocation dimensionId) {
        return loadedWorlds.containsKey(dimensionId);
    }

    /**
     * Gibt die geladene Welt zurück.
     */
    public static ServerLevel getLoadedWorld(ResourceLocation dimensionId) {
        CustomDimensionHolder holder = loadedWorlds.get(dimensionId);
        if (holder != null && holder.wasRegistered()) {
            return holder.world.asWorld();
        }
        return null;
    }

    /**
     * Gibt alle geladenen Dimensionen zurück.
     */
    public static Set<ResourceLocation> getLoadedDimensions() {
        return Collections.unmodifiableSet(loadedWorlds.keySet());
    }

    /**
     * Tick-Methode für das automatische Laden/Entladen.
     */
    private static void tick(MinecraftServer server) {
        Fantasy fantasy = Fantasy.get(server);
        Iterator<Map.Entry<ResourceLocation, CustomDimensionHolder>> it = loadedWorlds.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<ResourceLocation, CustomDimensionHolder> entry = it.next();
            CustomDimensionHolder holder = entry.getValue();

            if (holder.scheduledDelete()) {
                // Dimension entladen
                if (holder.deleteFinished(fantasy)) {
                    holder.executeComplete(server);
                    LOGGER.info("Dimension {} wurde entladen", entry.getKey());
                    it.remove();
                }
            } else if (!holder.wasRegistered()) {
                // Dimension laden
                try {
                    loadDimensionInternal(server, fantasy, holder);
                    holder.executeComplete(server);
                    LOGGER.info("Dimension {} wurde geladen", entry.getKey());
                } catch (Exception e) {
                    LOGGER.error("Fehler beim Laden der Dimension {}", entry.getKey(), e);
                    it.remove();
                }
            }
        }
    }

    /**
     * Interne Methode zum Laden einer Dimension.
     */
    private static void loadDimensionInternal(MinecraftServer server, Fantasy fantasy, CustomDimensionHolder holder) {
        // Weltdaten ZUERST kopieren, bevor wir die Dimension laden
        if (holder.externalWorldPath != null) {
            // Externe Welt kopieren
            copyExternalWorldData(server, holder.dimensionId, holder.externalWorldPath);
        } else {
            // Von bestehender Dimension kopieren
            copyWorldData(server, holder.dimensionId, holder.copyFromId);
        }

        // Dimension-Daten aus Registry holen
        Registry<DimensionType> dimTypeRegistry = server.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE);

        // Für externe Welten: Verwende Overworld-Typ
        // Für kopierte Dimensionen: Verwende den Original-Typ
        ResourceKey<DimensionType> dimTypeKey;
        if (holder.externalWorldPath != null) {
            // Externe Welten sind meist Overworld-artig
            dimTypeKey = ResourceKey.create(Registries.DIMENSION_TYPE,
                ResourceLocation.fromNamespaceAndPath("minecraft", "overworld"));
        } else {
            dimTypeKey = ResourceKey.create(Registries.DIMENSION_TYPE, holder.copyFromId);
        }

        DimensionType dimensionType = dimTypeRegistry.get(dimTypeKey).map(net.minecraft.core.Holder.Reference::value).orElse(null);
        if (dimensionType == null) {
            LOGGER.warn("Dimension-Typ {} nicht gefunden, verwende Standard-Overworld-Typ", dimTypeKey.location());
            ResourceKey<DimensionType> overworldKey = ResourceKey.create(Registries.DIMENSION_TYPE,
                ResourceLocation.fromNamespaceAndPath("minecraft", "overworld"));
            dimensionType = dimTypeRegistry.get(overworldKey).map(net.minecraft.core.Holder.Reference::value).orElse(null);
            if (dimensionType == null) {
                throw new RuntimeException("Kein gültiger DimensionType gefunden");
            }
        }

        // RuntimeWorldConfig erstellen
        // Verwende den ChunkGenerator der Overworld für geladene Welten
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            throw new RuntimeException("Overworld nicht gefunden!");
        }

        RuntimeWorldConfig config = new RuntimeWorldConfig()
                .setDimensionType(dimTypeRegistry.wrapAsHolder(dimensionType))
                .setMirrorOverworldGameRules(true)
                .setGenerator(overworld.getChunkSource().getGenerator()); // Verwende Overworld ChunkGenerator

        // Welt als PERSISTENT öffnen, damit die kopierten Daten geladen werden
        RuntimeWorldHandle handle = fantasy.getOrOpenPersistentWorld(holder.dimensionId, config);
        holder.register(handle);
    }

    /**
     * Kopiert Weltdaten von einer Dimension zur anderen.
     */
    private static boolean copyWorldData(MinecraftServer server, ResourceLocation targetId, ResourceLocation sourceId) {
        try {
            LevelStorageSource.LevelStorageAccess session = ((MinecraftServerAccess) server).getStorageSource();
            Path worldDir = session.getLevelPath(net.minecraft.world.level.storage.LevelResource.ROOT);

            Path sourcePath = getWorldPath(worldDir, sourceId);
            Path targetPath = getWorldPath(worldDir, targetId);

            if (!Files.exists(sourcePath)) {
                LOGGER.warn("Quell-Dimension {} existiert nicht", sourceId);
                return false;
            }

            // Ziel-Verzeichnis löschen falls vorhanden
            if (Files.exists(targetPath)) {
                FileUtils.deleteDirectory(targetPath.toFile());
            }

            // Verzeichnis erstellen
            Files.createDirectories(targetPath);

            // Daten kopieren
            copyDirectory(sourcePath, targetPath);
            LOGGER.info("Weltdaten von {} nach {} kopiert", sourceId, targetId);
            return true;

        } catch (IOException e) {
            LOGGER.error("Fehler beim Kopieren der Weltdaten", e);
            return false;
        }
    }

    /**
     * Kopiert eine externe Welt in die Server-Welten-Struktur.
     */
    private static boolean copyExternalWorldData(MinecraftServer server, ResourceLocation targetId, Path externalWorldPath) {
        try {
            LevelStorageSource.LevelStorageAccess session = ((MinecraftServerAccess) server).getStorageSource();
            Path worldDir = session.getLevelPath(net.minecraft.world.level.storage.LevelResource.ROOT);
            Path targetPath = getWorldPath(worldDir, targetId);

            if (!Files.exists(externalWorldPath)) {
                LOGGER.error("Externe Welt nicht gefunden: {}", externalWorldPath);
                return false;
            }

            // Ziel-Verzeichnis löschen falls vorhanden
            if (Files.exists(targetPath)) {
                FileUtils.deleteDirectory(targetPath.toFile());
            }

            // Verzeichnis erstellen
            Files.createDirectories(targetPath);

            // Welt-Daten kopieren
            // Prüfe ob es eine Minecraft-Welt ist (hat region-Ordner)
            Path regionSource = externalWorldPath.resolve("region");
            if (Files.exists(regionSource)) {
                // Standard Minecraft-Welt - kopiere region, data, etc.
                copyDirectory(regionSource, targetPath.resolve("region"));

                // Kopiere auch andere wichtige Ordner falls vorhanden
                copyDirectoryIfExists(externalWorldPath.resolve("data"), targetPath.resolve("data"));
                copyDirectoryIfExists(externalWorldPath.resolve("poi"), targetPath.resolve("poi"));
                copyDirectoryIfExists(externalWorldPath.resolve("entities"), targetPath.resolve("entities"));

                LOGGER.info("Externe Welt von {} nach {} kopiert", externalWorldPath, targetPath);
            } else {
                // Überprüfe auf Dimension-Struktur (dimensions/minecraft/overworld/region)
                Path dimensionOverworld = externalWorldPath.resolve("dimensions").resolve("minecraft").resolve("overworld");
                if (Files.exists(dimensionOverworld.resolve("region"))) {
                    copyDirectory(dimensionOverworld, targetPath);
                    LOGGER.info("Externe Dimension von {} nach {} kopiert", dimensionOverworld, targetPath);
                } else {
                    LOGGER.error("Keine gültige Minecraft-Welt gefunden in: {}", externalWorldPath);
                    return false;
                }
            }

            return true;

        } catch (IOException e) {
            LOGGER.error("Fehler beim Kopieren der externen Weltdaten von {}", externalWorldPath, e);
            return false;
        }
    }

    /**
     * Kopiert ein Verzeichnis falls es existiert.
     */
    private static void copyDirectoryIfExists(Path source, Path target) throws IOException {
        if (Files.exists(source)) {
            copyDirectory(source, target);
        }
    }

    /**
     * Kopiert ein Verzeichnis rekursiv.
     */
    private static void copyDirectory(Path source, Path target) throws IOException {
        try (Stream<Path> stream = Files.walk(source)) {
            stream.forEach(sourcePath -> {
                try {
                    Path targetPath = target.resolve(source.relativize(sourcePath));
                    if (Files.isDirectory(sourcePath)) {
                        Files.createDirectories(targetPath);
                    } else {
                        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    LOGGER.error("Fehler beim Kopieren von {}", sourcePath, e);
                }
            });
        }
    }

    /**
     * Gibt den Pfad zu einer Dimension zurück.
     */
    private static Path getWorldPath(Path worldDirectory, ResourceLocation dimensionId) {
        return worldDirectory.resolve("dimensions")
                .resolve(dimensionId.getNamespace())
                .resolve(dimensionId.getPath());
    }

    /**
     * Holder-Klasse für benutzerdefinierte Dimensionen.
     */
    public static class CustomDimensionHolder {
        public final ResourceLocation dimensionId;
        public final ResourceLocation copyFromId;
        private BiConsumer<MinecraftServer, CustomDimensionHolder> completionTask;
        public RuntimeWorldHandle world;
        private boolean scheduleDelete = false;
        public Path externalWorldPath = null; // Pfad zu einer externen Welt

        public CustomDimensionHolder(ResourceLocation dimensionId, ResourceLocation copyFromId) {
            this.dimensionId = dimensionId;
            this.copyFromId = copyFromId;
        }

        public void scheduleToDelete() {
            this.scheduleDelete = true;
        }

        public boolean scheduledDelete() {
            return scheduleDelete;
        }

        public boolean deleteFinished(Fantasy fantasy) {
            if (!scheduledDelete()) return false;

            if (wasRegistered()) {
                return fantasy.tickDeleteWorld(world.asWorld());
            }

            return true;
        }

        public boolean wasRegistered() {
            return this.world != null;
        }

        public void register(RuntimeWorldHandle handle) {
            this.world = handle;
        }

        public CustomDimensionHolder setCompleteTask(BiConsumer<MinecraftServer, CustomDimensionHolder> execute) {
            this.completionTask = execute;
            return this;
        }

        public void executeComplete(MinecraftServer server) {
            if (completionTask != null) {
                completionTask.accept(server, this);
            }
        }

        public ResourceKey<Level> getRegistryKey() {
            return world != null ? world.getRegistryKey() : ResourceKey.create(Registries.DIMENSION, dimensionId);
        }
    }
}
