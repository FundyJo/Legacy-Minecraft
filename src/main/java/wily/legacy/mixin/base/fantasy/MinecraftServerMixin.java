package wily.legacy.mixin.base.fantasy;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

/**
 * Mixin implementation for MinecraftServerAccess.
 * Exposes the MinecraftServer's levels map and storage source for the Fantasy system.
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerAccess {

    @Shadow
    private Map<ResourceKey<Level>, ServerLevel> levels;

    @Shadow
    @Final
    protected LevelStorageSource.LevelStorageAccess storageSource;

    @Override
    public Map<ResourceKey<Level>, ServerLevel> getLevels() {
        return levels;
    }

    @Override
    public LevelStorageSource.LevelStorageAccess getStorageSource() {
        return storageSource;
    }
}
