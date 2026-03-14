package wily.legacy.fantasy;

import wily.factoryapi.FactoryEvent;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import wily.legacy.fantasy.util.TransientChunkGenerator;
import wily.legacy.fantasy.util.VoidChunkGenerator;

public final class FantasyInitializer {
    private static boolean initialized = false;

    static {
        FactoryEvent.setup(FantasyInitializer::initialize);
    }

    // This method is called by the platform-specific entry point
    // or automatically via the static block
    public static void init() {
        // Ensure class is loaded and static block executes
        initialize();
    }

    private static synchronized void initialize() {
        if (initialized) {
            return;
        }
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, ResourceLocation.fromNamespaceAndPath(Fantasy.ID, "void"), VoidChunkGenerator.CODEC);
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, ResourceLocation.fromNamespaceAndPath(Fantasy.ID, "transient"), TransientChunkGenerator.CODEC);
        initialized = true;
    }
}

