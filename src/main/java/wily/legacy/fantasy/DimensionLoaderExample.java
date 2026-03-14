package wily.legacy.fantasy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

/**
 * Beispiel-Klasse zur Verwendung des DimensionLoaders.
 *
 * Diese Klasse zeigt verschiedene Anwendungsfälle für das dynamische Laden
 * und Entladen von Dimensionen.
 */
public class DimensionLoaderExample {
    private static final Logger LOGGER = LogManager.getLogger(DimensionLoaderExample.class);

    /**
     * Beispiel 1: Eine Arena-Welt laden
     *
     * Lädt eine temporäre Kopie der Nether-Dimension als "arena".
     */
    public static void loadArenaWorld(MinecraftServer server) {
        ResourceLocation arenaId = ResourceLocation.fromNamespaceAndPath("legacy", "arena");
        ResourceLocation netherId = ResourceLocation.fromNamespaceAndPath("minecraft", "the_nether");

        DimensionLoader.loadDimension(server, arenaId, netherId, (srv, holder) -> {
            LOGGER.info("Arena-Welt wurde erfolgreich geladen!");

            // Optionale Nachbearbeitung
            ServerLevel arenaWorld = holder.world.asWorld();
            if (arenaWorld != null) {
                // Hier können Sie weitere Einstellungen vornehmen
                LOGGER.info("Arena-Welt ist bereit: {}", arenaWorld.dimension().location());
            }
        });
    }

    /**
     * Beispiel 2: Eine Minigame-Welt laden
     *
     * Lädt eine temporäre Kopie der Overworld-Dimension für ein Minispiel.
     */
    public static void loadMinigameWorld(MinecraftServer server) {
        ResourceLocation minigameId = ResourceLocation.fromNamespaceAndPath("legacy", "minigame_world");
        ResourceLocation overworldId = ResourceLocation.fromNamespaceAndPath("minecraft", "overworld");

        DimensionLoader.loadDimension(server, minigameId, overworldId, (srv, holder) -> {
            LOGGER.info("Minigame-Welt geladen!");

            // Spieler zur Minigame-Welt teleportieren (Beispiel)
            ServerLevel minigameWorld = holder.world.asWorld();
            // ... Teleportationslogik hier
        });
    }

    /**
     * Beispiel 2b: Eine externe Welt laden (z.B. cavern_largeplus)
     *
     * Lädt eine lokal gespeicherte Welt als neue Dimension.
     */
    public static void loadExternalCavernWorld(MinecraftServer server) {
        ResourceLocation cavernId = ResourceLocation.fromNamespaceAndPath("legacy", "cavern");
        String worldPath = "C:\\Users\\timos\\Documents\\testing\\cavern_largeplus";

        DimensionLoader.loadExternalWorld(server, cavernId, worldPath, (srv, holder) -> {
            LOGGER.info("Cavern-Welt erfolgreich geladen!");

            ServerLevel cavernWorld = holder.world.asWorld();
            if (cavernWorld != null) {
                LOGGER.info("Cavern-Welt ist bereit: {}", cavernWorld.dimension().location());
                // Setze Welteinstellungen
                cavernWorld.setDayTime(18000); // Nacht
            }
        });
    }

    /**
     * Beispiel 2c: Eine externe Welt mit Path-Objekt laden
     */
    public static void loadExternalWorldWithPath(MinecraftServer server) {
        ResourceLocation worldId = ResourceLocation.fromNamespaceAndPath("legacy", "custom_map");
        Path worldPath = Path.of("C:", "Users", "timos", "Documents", "testing", "cavern_largeplus");

        DimensionLoader.loadExternalWorld(server, worldId, worldPath, (srv, holder) -> {
            LOGGER.info("Externe Welt geladen von: {}", worldPath);
        });
    }

    /**
     * Beispiel 3: Eine Welt entladen
     *
     * Entlädt eine zuvor geladene Arena-Welt.
     */
    public static void unloadArenaWorld() {
        ResourceLocation arenaId = ResourceLocation.fromNamespaceAndPath("legacy", "arena");

        DimensionLoader.unloadDimension(arenaId, (srv, holder) -> {
            LOGGER.info("Arena-Welt wurde erfolgreich entladen!");

            // Cleanup-Aktionen durchführen
            // z.B. Spielerdaten speichern, Statistiken aktualisieren, etc.
        });
    }

    /**
     * Beispiel 4: Mehrere Event-Welten verwalten
     *
     * Zeigt, wie man mehrere Event-Welten gleichzeitig verwalten kann.
     */
    public static void manageEventWorlds(MinecraftServer server) {
        // Event 1: PvP-Arena
        ResourceLocation pvpArenaId = ResourceLocation.fromNamespaceAndPath("legacy", "pvp_arena");
        DimensionLoader.loadDimension(server, pvpArenaId,
            ResourceLocation.fromNamespaceAndPath("minecraft", "overworld"));

        // Event 2: Boss-Fight
        ResourceLocation bossFightId = ResourceLocation.fromNamespaceAndPath("legacy", "boss_fight");
        DimensionLoader.loadDimension(server, bossFightId,
            ResourceLocation.fromNamespaceAndPath("minecraft", "the_end"));

        // Event 3: Race-Track
        ResourceLocation raceTrackId = ResourceLocation.fromNamespaceAndPath("legacy", "race_track");
        DimensionLoader.loadDimension(server, raceTrackId,
            ResourceLocation.fromNamespaceAndPath("minecraft", "overworld"));

        LOGGER.info("Alle Event-Welten werden geladen...");
    }

    /**
     * Beispiel 5: Prüfen, ob eine Dimension geladen ist
     */
    public static boolean isArenaReady() {
        ResourceLocation arenaId = ResourceLocation.fromNamespaceAndPath("legacy", "arena");
        return DimensionLoader.isDimensionLoaded(arenaId);
    }

    /**
     * Beispiel 6: Alle geladenen Dimensionen auflisten
     */
    public static void listLoadedDimensions() {
        LOGGER.info("Aktuell geladene Dimensionen:");
        for (ResourceLocation dimId : DimensionLoader.getLoadedDimensions()) {
            LOGGER.info(" - {}", dimId);
        }
    }

    /**
     * Beispiel 7: Spieler zu einer geladenen Dimension teleportieren
     */
    public static void teleportPlayerToArena(ServerPlayer player) {
        ResourceLocation arenaId = ResourceLocation.fromNamespaceAndPath("legacy", "arena");
        ServerLevel arenaWorld = DimensionLoader.getLoadedWorld(arenaId);

        if (arenaWorld != null) {
            teleportPlayerToWorld(player, arenaWorld);
            LOGGER.info("Spieler {} wird zur Arena teleportiert", player.getName().getString());
        } else {
            LOGGER.warn("Arena-Welt ist nicht geladen!");
        }
    }

    /**
     * Hilfsmethode: Teleportiert einen Spieler zu einer Welt
     */
    private static void teleportPlayerToWorld(ServerPlayer player, ServerLevel targetWorld) {
        // Spawn-Position der Zielwelt (0, 64, 0 als Fallback)
        net.minecraft.core.BlockPos spawnPos = new net.minecraft.core.BlockPos(0, 64, 0);

        // Position mit Zentrierung
        net.minecraft.world.phys.Vec3 targetPos = new net.minecraft.world.phys.Vec3(
            spawnPos.getX() + 0.5,
            spawnPos.getY(),
            spawnPos.getZ() + 0.5
        );

        // Teleportation durchführen
        player.setServerLevel(targetWorld);
        player.setPos(targetPos);
        player.connection.teleport(targetPos.x, targetPos.y, targetPos.z, player.getYRot(), player.getXRot());
    }

    /**
     * Beispiel 8: Eine benutzerdefinierte Dimension mit spezifischer Konfiguration
     */
    public static void loadCustomDimension(MinecraftServer server) {
        ResourceLocation customId = ResourceLocation.fromNamespaceAndPath("legacy", "custom_world");
        ResourceLocation baseId = ResourceLocation.fromNamespaceAndPath("minecraft", "overworld");

        DimensionLoader.loadDimension(server, customId, baseId, (srv, holder) -> {
            ServerLevel customWorld = holder.world.asWorld();

            if (customWorld != null) {
                // Benutzerdefinierte Welteinstellungen
                customWorld.setWeatherParameters(0, 0, false, false);
                customWorld.setDayTime(6000); // Mittag

                LOGGER.info("Benutzerdefinierte Welt mit speziellen Einstellungen geladen");
            }
        });
    }

    /**
     * Beispiel 9: Event-System - Welt laden wenn Event startet, entladen wenn es endet
     */
    public static class EventWorldManager {
        private static final ResourceLocation EVENT_WORLD_ID =
            ResourceLocation.fromNamespaceAndPath("legacy", "current_event");

        public static void startEvent(MinecraftServer server, ResourceLocation baseDimension) {
            if (DimensionLoader.isDimensionLoaded(EVENT_WORLD_ID)) {
                LOGGER.warn("Ein Event läuft bereits!");
                return;
            }

            DimensionLoader.loadDimension(server, EVENT_WORLD_ID, baseDimension, (srv, holder) -> {
                LOGGER.info("Event-Welt gestartet!");
                // Event-Start-Logik
            });
        }

        public static void endEvent() {
            if (!DimensionLoader.isDimensionLoaded(EVENT_WORLD_ID)) {
                LOGGER.warn("Kein Event läuft aktuell!");
                return;
            }

            DimensionLoader.unloadDimension(EVENT_WORLD_ID, (srv, holder) -> {
                LOGGER.info("Event beendet, Welt wird aufgeräumt");
                // Event-End-Logik, z.B. Belohnungen verteilen
            });
        }
    }

    /**
     * Beispiel 10: Integration mit einem Minigame-System
     */
    public static class MinigameIntegration {

        public static void onMinigameStart(MinecraftServer server, String minigameName) {
            ResourceLocation minigameId = ResourceLocation.fromNamespaceAndPath("legacy", "minigame_" + minigameName);
            ResourceLocation baseWorld = ResourceLocation.fromNamespaceAndPath("minecraft", "overworld");

            DimensionLoader.loadDimension(server, minigameId, baseWorld, (srv, holder) -> {
                LOGGER.info("Minigame {} gestartet in Welt {}", minigameName, holder.dimensionId);

                // Minigame-Setup
                ServerLevel gameWorld = holder.world.asWorld();
                if (gameWorld != null) {
                    setupMinigameWorld(gameWorld, minigameName);
                }
            });
        }

        public static void onMinigameEnd(String minigameName) {
            ResourceLocation minigameId = ResourceLocation.fromNamespaceAndPath("legacy", "minigame_" + minigameName);

            DimensionLoader.unloadDimension(minigameId, (srv, holder) -> {
                LOGGER.info("Minigame {} beendet", minigameName);
                // Cleanup und Statistiken speichern
            });
        }

        private static void setupMinigameWorld(ServerLevel world, String minigameName) {
            // Minigame-spezifische Welteinstellungen
            world.setDayTime(6000);
            // Weitere Einstellungen...
        }
    }

    /**
     * Beispiel 11: Praktische Beispiele für verschiedene Welttypen
     */
    public static class PracticalExamples {

        /**
         * Lädt die cavern_largeplus Welt
         */
        public static void loadCavernWorld(MinecraftServer server) {
            ResourceLocation cavernId = ResourceLocation.fromNamespaceAndPath("legacy", "cavern_large");
            String cavernPath = "C:\\Users\\timos\\Documents\\testing\\cavern_largeplus";

            DimensionLoader.loadExternalWorld(server, cavernId, cavernPath, (srv, holder) -> {
                ServerLevel world = holder.world.asWorld();
                if (world != null) {
                    LOGGER.info("✓ Cavern-Welt geladen!");
                    // Optional: Welteinstellungen anpassen
                    world.setDayTime(6000);
                    world.setWeatherParameters(0, 0, false, false);
                }
            });
        }

        /**
         * Lädt mehrere externe Welten gleichzeitig
         */
        public static void loadMultipleExternalWorlds(MinecraftServer server) {
            // Welt 1: Cavern
            DimensionLoader.loadExternalWorld(server,
                ResourceLocation.fromNamespaceAndPath("legacy", "cavern"),
                "C:\\Users\\timos\\Documents\\testing\\cavern_largeplus");

            // Welt 2: Eine andere benutzerdefinierte Welt
            DimensionLoader.loadExternalWorld(server,
                ResourceLocation.fromNamespaceAndPath("legacy", "custom_arena"),
                "C:\\Users\\timos\\Documents\\worlds\\arena_map");

            // Welt 3: Noch eine Welt
            DimensionLoader.loadExternalWorld(server,
                ResourceLocation.fromNamespaceAndPath("legacy", "adventure_map"),
                "C:\\Users\\timos\\Documents\\worlds\\adventure");

            LOGGER.info("Alle externen Welten werden geladen...");
        }

        /**
         * Entlädt die Cavern-Welt
         */
        public static void unloadCavernWorld() {
            ResourceLocation cavernId = ResourceLocation.fromNamespaceAndPath("legacy", "cavern_large");

            DimensionLoader.unloadDimension(cavernId, (srv, holder) -> {
                LOGGER.info("✓ Cavern-Welt wurde entladen und aufgeräumt");
            });
        }

        /**
         * Überprüft ob die Cavern-Welt geladen ist und gibt die Welt zurück
         */
        public static ServerLevel getCavernWorld() {
            ResourceLocation cavernId = ResourceLocation.fromNamespaceAndPath("legacy", "cavern_large");
            return DimensionLoader.getLoadedWorld(cavernId);
        }
    }
}
