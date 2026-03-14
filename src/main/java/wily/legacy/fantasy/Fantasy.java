package wily.legacy.fantasy;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import wily.factoryapi.FactoryEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wily.legacy.mixin.base.fantasy.MinecraftServerAccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Fantasy is a library that allows for dimensions to be created and destroyed at runtime on the server.
 * It supports both temporary dimensions which do not get saved, as well as persistent dimensions which can be safely
 * used across server restarts.
 *
 * @see Fantasy#get(MinecraftServer)
 * @see Fantasy#openTemporaryWorld(RuntimeWorldConfig)
 * @see Fantasy#getOrOpenPersistentWorld(ResourceLocation, RuntimeWorldConfig)
 */
public final class Fantasy {
    public static final Logger LOGGER = LogManager.getLogger(Fantasy.class);
    public static final String ID = "fantasy";
    public static final ResourceKey<DimensionType> DEFAULT_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLocation.fromNamespaceAndPath(Fantasy.ID, "default"));

    private static Fantasy instance;

    private final MinecraftServer server;
    private final MinecraftServerAccess serverAccess;

    private final RuntimeWorldManager worldManager;

    private final Set<ServerLevel> deletionQueue = new ReferenceOpenHashSet<>();
    private final Set<ServerLevel> unloadingQueue = new ReferenceOpenHashSet<>();

    static {
        FactoryEvent.preServerTick(server -> {
            Fantasy fantasy = get(server);
            fantasy.tick();
        });

        FactoryEvent.serverStopping(server -> {
            Fantasy fantasy = get(server);
            fantasy.onServerStopping();
        });
    }

    private Fantasy(MinecraftServer server) {
        this.server = server;
        this.serverAccess = (MinecraftServerAccess) server;

        this.worldManager = new RuntimeWorldManager(server);
    }

    /**
     * Gets the {@link Fantasy} instance for the given server instance.
     *
     * @param server the server to work with
     * @return the {@link Fantasy} instance to work with runtime dimensions
     */
    public static Fantasy get(MinecraftServer server) {
        Preconditions.checkState(server.isSameThread(), "cannot create worlds from off-thread!");

        if (instance == null || instance.server != server) {
            instance = new Fantasy(server);
        }

        return instance;
    }

    private void tick() {
        Set<ServerLevel> deletionQueue = this.deletionQueue;
        if (!deletionQueue.isEmpty()) {
            deletionQueue.removeIf(this::tickDeleteWorld);
        }

        Set<ServerLevel> unloadingQueue = this.unloadingQueue;
        if (!unloadingQueue.isEmpty()) {
            unloadingQueue.removeIf(this::tickUnloadWorld);
        }
    }

    /**
     * Creates a new temporary world with the given {@link RuntimeWorldConfig} that will not be saved and will be
     * deleted when the server exits.
     * <p>
     * The created world is returned asynchronously through a {@link RuntimeWorldHandle}.
     * This handle can be used to acquire the {@link ServerLevel} object through {@link RuntimeWorldHandle#asWorld()},
     * as well as to delete the world through {@link RuntimeWorldHandle#delete()}.
     *
     * @param config the config with which to construct this temporary world
     * @return a future providing the created world
     */
    public RuntimeWorldHandle openTemporaryWorld(RuntimeWorldConfig config) {
        return this.openTemporaryWorld(generateTemporaryWorldKey(), config);
    }

    /**
     * Creates a new temporary world with the given identifier and {@link RuntimeWorldConfig} that will not be saved and will be
     * deleted when the server exits.
     * <p>
     * The created world is returned asynchronously through a {@link RuntimeWorldHandle}.
     * This handle can be used to acquire the {@link ServerLevel} object through {@link RuntimeWorldHandle#asWorld()},
     * as well as to delete the world through {@link RuntimeWorldHandle#delete()}.
     *
     * @param key the unique identifier for this dimension
     * @param config the config with which to construct this temporary world
     * @return a future providing the created world
     */
    public RuntimeWorldHandle openTemporaryWorld(ResourceLocation key, RuntimeWorldConfig config) {
        RuntimeWorld world = this.addTemporaryWorld(key, config);
        return new RuntimeWorldHandle(this, world);
    }

    /**
     * Gets or creates a new persistent world with the given identifier and {@link RuntimeWorldConfig}. These worlds
     * will be saved to disk and can be restored after a server restart.
     * <p>
     * If a world with this identifier exists already, it will be returned and no new world will be constructed.
     * <p>
     * <b>Note!</b> These persistent worlds will not be automatically restored! This function
     * must be called after a server restart with the relevant identifier and configuration such that it can be loaded.
     * <p>
     * The created world is returned asynchronously through a {@link RuntimeWorldHandle}.
     * This handle can be used to acquire the {@link ServerLevel} object through {@link RuntimeWorldHandle#asWorld()},
     * as well as to delete the world through {@link RuntimeWorldHandle#delete()}.
     *
     * @param key the unique identifier for this dimension
     * @param config the config with which to construct this persistent world
     * @return a future providing the created world
     */
    public RuntimeWorldHandle getOrOpenPersistentWorld(ResourceLocation key, RuntimeWorldConfig config) {
        ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION, key);

        ServerLevel world = this.server.getLevel(worldKey);
        if (world == null) {
            world = this.addPersistentWorld(key, config);
        } else {
            this.deletionQueue.remove(world);
            this.unloadingQueue.remove(world);
        }

        return new RuntimeWorldHandle(this, world);
    }

    private RuntimeWorld addPersistentWorld(ResourceLocation key, RuntimeWorldConfig config) {
        ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION, key);
        return this.worldManager.add(worldKey, config, RuntimeWorld.Style.PERSISTENT);
    }

    private RuntimeWorld addTemporaryWorld(ResourceLocation key, RuntimeWorldConfig config) {
        ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION, key);

        try {
            LevelStorageSource.LevelStorageAccess session = this.serverAccess.getStorageSource();
            FileUtils.forceDeleteOnExit(session.getDimensionPath(worldKey).toFile());
        } catch (IOException ignored) {
        }

        return this.worldManager.add(worldKey, config, RuntimeWorld.Style.TEMPORARY);
    }

    void enqueueWorldDeletion(ServerLevel world) {
        this.server.execute(() -> {
            world.getChunkSource().deactivateTicketsOnClosing();
            world.noSave = true;
            this.kickPlayers(world);
            this.deletionQueue.add(world);
        });
    }

    void enqueueWorldUnloading(ServerLevel world) {
        this.server.execute(() -> {
            world.noSave = false;
            world.getChunkSource().deactivateTicketsOnClosing();
            world.getChunkSource().tick(() -> true, false);
            this.kickPlayers(world);
            this.unloadingQueue.add(world);
        });
    }

    public boolean tickDeleteWorld(ServerLevel world) {
        //if (this.isWorldActive(world)) {
        this.kickPlayers(world);
        this.worldManager.delete(world);
        return true;
        //} else {
        //    this.kickPlayers(world);
        //    return false;
        //}
    }

    public boolean tickUnloadWorld(ServerLevel world) {
        if (this.isWorldActive(world) && !world.getChunkSource().chunkMap.hasWork()) {
            this.worldManager.unload(world);
            return true;
        } else {
            this.kickPlayers(world);
            return false;
        }
    }

    private void kickPlayers(ServerLevel world) {
        if (world.players().isEmpty()) {
            return;
        }

        ServerLevel spawnWorld = this.server.findRespawnDimension();
        LevelData.RespawnData spawnPoint = this.server.getRespawnData();

        List<ServerPlayer> players = new ArrayList<>(world.players());

        for (ServerPlayer player : players) {
            Vec3 pos = player.adjustSpawnLocation(spawnWorld, spawnPoint.pos()).getBottomCenter();
            TeleportTransition target = new TeleportTransition(spawnWorld, pos, Vec3.ZERO, spawnPoint.yaw(), spawnPoint.pitch(), TeleportTransition.DO_NOTHING);

            player.teleport(target);
        }
    }

    private boolean isWorldActive(ServerLevel world) {
        return world.players().isEmpty() && world.getChunkSource().getLoadedChunksCount() <= 0;
    }

    private void onServerStopping() {
        List<RuntimeWorld> temporaryWorlds = this.collectTemporaryWorlds();
        for (RuntimeWorld temporary : temporaryWorlds) {
            this.kickPlayers(temporary);
            this.worldManager.delete(temporary);
        }
    }

    private List<RuntimeWorld> collectTemporaryWorlds() {
        List<RuntimeWorld> temporaryWorlds = new ArrayList<>();
        for (ServerLevel world : this.server.getAllLevels()) {
            if (world instanceof RuntimeWorld runtimeWorld) {
                if (runtimeWorld.style == RuntimeWorld.Style.TEMPORARY) {
                    temporaryWorlds.add(runtimeWorld);
                }
            }
        }
        return temporaryWorlds;
    }

    private static ResourceLocation generateTemporaryWorldKey() {
        String key = RandomStringUtils.random(16, "abcdefghijklmnopqrstuvwxyz0123456789");
        return ResourceLocation.fromNamespaceAndPath(Fantasy.ID, key);
    }
}
