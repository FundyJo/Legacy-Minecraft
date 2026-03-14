package wily.legacy.minigame;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import wily.legacy.Legacy4J;
import wily.legacy.minigame.controller.AbstractMinigameController;
import wily.legacy.minigame.controller.FistfightMinigameController;
import wily.legacy.minigame.controller.GlideMinigameController;
import wily.legacy.minigame.controller.LobbyMinigameController;
import wily.legacy.minigame.controller.NoneMinigameController;
import wily.legacy.minigame.controller.TumbleMinigameController;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public final class Minigame<T extends AbstractMinigameController<T>> {
    private static final Minigame<?>[] VALUES = new Minigame[8];
    private static int nextId = 0;

    public static final Minigame<NoneMinigameController> NONE = register("none", NoneMinigameController::new);
    public static final Minigame<LobbyMinigameController> LOBBY = register("lobby", LobbyMinigameController::new);
    public static final Minigame<GlideMinigameController> GLIDE = register("glide", GlideMinigameController::new);
    public static final Minigame<FistfightMinigameController> FISTFIGHT = register("fistfight", FistfightMinigameController::new);
    public static final Minigame<TumbleMinigameController> TUMBLE = register("tumble", TumbleMinigameController::new);

    private final int id;
    private final ResourceLocation name;
    private final Function<ServerLevel, T> controllerFactory;

    private Minigame(int id, ResourceLocation name, Function<ServerLevel, T> controllerFactory) {
        this.id = id;
        this.name = name;
        this.controllerFactory = controllerFactory;
    }

    private static <T extends AbstractMinigameController<T>> Minigame<T> register(String name, Function<ServerLevel, T> factory) {
        int id = nextId++;
        Minigame<T> minigame = new Minigame<>(id, Legacy4J.createModLocation(name), factory);
        VALUES[id] = minigame;
        return minigame;
    }

    public int getId() {
        return id;
    }

    public ResourceLocation getLocation() {
        return name;
    }

    public String getName() {
        return name.getPath();
    }

    public Component getDisplayName() {
        return Component.translatable("legacy.minigame." + getName());
    }

    public T newController(ServerLevel level) {
        return controllerFactory.apply(level);
    }

    public static Minigame<?> fromId(int id) {
        if (id >= 0 && id < nextId) return VALUES[id];
        return NONE;
    }

    public static Optional<Minigame<?>> fromName(String name) {
        return Arrays.stream(values()).filter(m -> m.getName().equals(name)).findFirst();
    }

    public static Minigame<?>[] values() {
        return Arrays.copyOf(VALUES, nextId);
    }

    @Override
    public String toString() {
        return "Minigame{" + getName() + "}";
    }
}
