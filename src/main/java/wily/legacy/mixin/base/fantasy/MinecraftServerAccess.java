package wily.legacy.mixin.base.fantasy;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.util.Map;

/**
 * Mixin accessor interface for MinecraftServer.
 * Exposes private fields needed by the Fantasy dimension management system.
 */
public interface MinecraftServerAccess {
    Map<ResourceKey<Level>, ServerLevel> getLevels();
    LevelStorageSource.LevelStorageAccess getStorageSource();
}
