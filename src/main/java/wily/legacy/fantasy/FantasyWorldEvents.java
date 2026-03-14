package wily.legacy.fantasy;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Custom world events for Fantasy library.
 * These are platform-agnostic events for world loading and unloading.
 */
public final class FantasyWorldEvents {
    private FantasyWorldEvents() {}

    public static final WorldEvent LOAD = new WorldEvent();
    public static final WorldEvent UNLOAD = new WorldEvent();

    public static class WorldEvent {
        private final List<BiConsumer<MinecraftServer, ServerLevel>> listeners = new CopyOnWriteArrayList<>();

        public void register(BiConsumer<MinecraftServer, ServerLevel> listener) {
            listeners.add(listener);
        }

        public void invoke(MinecraftServer server, ServerLevel world) {
            for (BiConsumer<MinecraftServer, ServerLevel> listener : listeners) {
                listener.accept(server, world);
            }
        }
    }
}
