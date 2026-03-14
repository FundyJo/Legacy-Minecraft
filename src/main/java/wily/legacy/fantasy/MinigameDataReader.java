package wily.legacy.fantasy;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Liest Minigame-Daten aus einer minigame.dat Datei.
 */
public class MinigameDataReader {
    private static final Logger LOGGER = LogManager.getLogger(MinigameDataReader.class);

    private final String mapName;
    private final List<SpawnPoint> centerTeleportSpawns = new ArrayList<>();
    private final List<SpawnPoint> randomTeleportSpawns = new ArrayList<>();
    private final List<ChestLocation> standardChests = new ArrayList<>();
    private final List<ChestLocation> powerfulChests = new ArrayList<>();
    private final List<ChestLocation> centerChests = new ArrayList<>();
    private BorderData border;

    /**
     * Lädt Minigame-Daten aus einer minigame.dat Datei.
     *
     * @param minigameDataPath Pfad zur minigame.dat Datei
     * @return MinigameDataReader mit geladenen Daten oder null bei Fehler
     */
    public static MinigameDataReader load(Path minigameDataPath) {
        try {
            File file = minigameDataPath.toFile();
            if (!file.exists()) {
                LOGGER.warn("minigame.dat nicht gefunden: {}", minigameDataPath);
                return null;
            }

            // Versuche zuerst unkomprimiertes NBT (normales Format)
            CompoundTag rootTag;
            try {
                // Verwende readUncompressed für nicht-komprimierte NBT-Dateien
                try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
                    java.io.DataInputStream dis = new java.io.DataInputStream(fis);
                    rootTag = NbtIo.read(dis, net.minecraft.nbt.NbtAccounter.unlimitedHeap());
                }
                LOGGER.info("✅ minigame.dat geladen (unkomprimiert)");
            } catch (IOException e) {
                // Fallback: Versuche komprimiertes NBT (GZIP)
                LOGGER.debug("Versuch mit komprimiertem Format...");
                rootTag = NbtIo.readCompressed(file.toPath(), net.minecraft.nbt.NbtAccounter.unlimitedHeap());
                LOGGER.info("✅ minigame.dat geladen (komprimiert)");
            }

            return new MinigameDataReader(rootTag);

        } catch (IOException e) {
            LOGGER.error("Fehler beim Lesen der minigame.dat - I/O Fehler", e);
            return null;
        } catch (Exception e) {
            LOGGER.error("Unerwarteter Fehler beim Parsen der minigame.dat", e);
            return null;
        }
    }

    private MinigameDataReader(CompoundTag rootTag) {
        // Map-Name auslesen
        this.mapName = rootTag.getString("mapName").orElse("unknown");

        // Border auslesen
        if (rootTag.contains("border")) {
            CompoundTag borderTag = rootTag.getCompound("border").orElseThrow();

            // min und max sind direkte Listen [x, y, z], nicht CompoundTags
            this.border = new BorderData(
                readPositionFromList(borderTag, "min"),
                readPositionFromList(borderTag, "max")
            );
        }

        // Spawns auslesen
        if (rootTag.contains("spawns")) {
            CompoundTag spawnsTag = rootTag.getCompound("spawns").orElseThrow();

            // CenterTP spawns
            if (spawnsTag.contains("centerTP")) {
                ListTag centerTPList = spawnsTag.getList("centerTP").orElseThrow();
                for (int i = 0; i < centerTPList.size(); i++) {
                    CompoundTag spawnTag = centerTPList.getCompound(i).orElseThrow();
                    centerTeleportSpawns.add(readSpawnPoint(spawnTag));
                }
            }

            // RandomTP spawns
            if (spawnsTag.contains("randomTP")) {
                ListTag randomTPList = spawnsTag.getList("randomTP").orElseThrow();
                for (int i = 0; i < randomTPList.size(); i++) {
                    CompoundTag spawnTag = randomTPList.getCompound(i).orElseThrow();
                    randomTeleportSpawns.add(readSpawnPoint(spawnTag));
                }
            }
        }

        // Chests auslesen
        if (rootTag.contains("chests")) {
            CompoundTag chestsTag = rootTag.getCompound("chests").orElseThrow();

            // Standard Chests
            if (chestsTag.contains("standard")) {
                ListTag standardList = chestsTag.getList("standard").orElseThrow();
                for (int i = 0; i < standardList.size(); i++) {
                    CompoundTag chestTag = standardList.getCompound(i).orElseThrow();
                    standardChests.add(readChestLocation(chestTag));
                }
            }

            // Powerful Chests
            if (chestsTag.contains("powerful")) {
                ListTag powerfulList = chestsTag.getList("powerful").orElseThrow();
                for (int i = 0; i < powerfulList.size(); i++) {
                    CompoundTag chestTag = powerfulList.getCompound(i).orElseThrow();
                    powerfulChests.add(readChestLocation(chestTag));
                }
            }

            // Center Chests
            if (chestsTag.contains("center")) {
                ListTag centerList = chestsTag.getList("center").orElseThrow();
                for (int i = 0; i < centerList.size(); i++) {
                    CompoundTag chestTag = centerList.getCompound(i).orElseThrow();
                    centerChests.add(readChestLocation(chestTag));
                }
            }
        }

        LOGGER.info("Minigame-Daten geladen: {} - {} CenterTP Spawns, {} RandomTP Spawns",
            mapName, centerTeleportSpawns.size(), randomTeleportSpawns.size());
    }

    private static BlockPos readPositionFromList(CompoundTag tag, String key) {
        // Liest eine Position direkt aus einer Liste (z.B. border.min = [x, y, z])
        if (tag.contains(key)) {
            ListTag posList = tag.getList(key).orElse(new ListTag());
            if (posList.size() >= 3) {
                double dx = posList.getDouble(0).orElse(0.0);
                double dy = posList.getDouble(1).orElse(0.0);
                double dz = posList.getDouble(2).orElse(0.0);
                int x = (int) Math.floor(dx);
                int y = (int) Math.floor(dy);
                int z = (int) Math.floor(dz);
                LOGGER.debug("readPositionFromList: {} -> [{}, {}, {}] (floored)", key, x, y, z);
                return new BlockPos(x, y, z);
            }
        }
        return BlockPos.ZERO;
    }

    private static BlockPos readPosition(CompoundTag posTag) {
        if (posTag.contains("pos")) {
            ListTag posList = posTag.getList("pos").orElseThrow();
            double dx = posList.getDouble(0).orElse(0.0);
            double dy = posList.getDouble(1).orElse(0.0);
            double dz = posList.getDouble(2).orElse(0.0);
            int x = (int) Math.floor(dx);
            int y = (int) Math.floor(dy);
            int z = (int) Math.floor(dz);
            LOGGER.debug("readPosition: pos -> [{}, {}, {}] (from {} , {}, {})", x, y, z, dx, dy, dz);
            return new BlockPos(x, y, z);
        }
        return BlockPos.ZERO;
    }

    private static SpawnPoint readSpawnPoint(CompoundTag spawnTag) {
        BlockPos pos = readPosition(spawnTag);
        ListTag tagsList = spawnTag.getList("tags").orElse(new ListTag());
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < tagsList.size(); i++) {
            tagsList.getString(i).ifPresent(tags::add);
        }
        return new SpawnPoint(pos, tags);
    }

    private static ChestLocation readChestLocation(CompoundTag chestTag) {
        BlockPos pos = readPosition(chestTag);
        ListTag tagsList = chestTag.getList("tags").orElse(new ListTag());
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < tagsList.size(); i++) {
            tagsList.getString(i).ifPresent(tags::add);
        }
        return new ChestLocation(pos, tags);
    }

    // Getters
    public String getMapName() {
        return mapName;
    }

    public List<SpawnPoint> getCenterTeleportSpawns() {
        return centerTeleportSpawns;
    }

    public List<SpawnPoint> getRandomTeleportSpawns() {
        return randomTeleportSpawns;
    }

    public List<ChestLocation> getStandardChests() {
        return standardChests;
    }

    public List<ChestLocation> getPowerfulChests() {
        return powerfulChests;
    }

    public List<ChestLocation> getCenterChests() {
        return centerChests;
    }

    public BorderData getBorder() {
        return border;
    }

    // Daten-Klassen
    public static class SpawnPoint {
        public final BlockPos pos;
        public final List<String> tags;

        public SpawnPoint(BlockPos pos, List<String> tags) {
            this.pos = pos;
            this.tags = tags;
        }
    }

    public static class ChestLocation {
        public final BlockPos pos;
        public final List<String> tags;

        public ChestLocation(BlockPos pos, List<String> tags) {
            this.pos = pos;
            this.tags = tags;
        }
    }

    public static class BorderData {
        public final BlockPos min;
        public final BlockPos max;

        public BorderData(BlockPos min, BlockPos max) {
            this.min = min;
            this.max = max;
        }
    }
}
