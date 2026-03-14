package wily.legacy.minigame;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record MinigameData(
        ResourceLocation mapId,
        String displayName,
        int maxPlayers,
        int minPlayers,
        int rounds,
        List<ResourceLocation> availableMaps
) {
    public static final MinigameData EMPTY = new MinigameData(
            ResourceLocation.withDefaultNamespace("empty"),
            "Unknown",
            8,
            2,
            1,
            List.of()
    );

    public MinigameData withMapId(ResourceLocation newMapId) {
        return new MinigameData(newMapId, displayName, maxPlayers, minPlayers, rounds, availableMaps);
    }

    public MinigameData withRounds(int newRounds) {
        return new MinigameData(mapId, displayName, maxPlayers, minPlayers, newRounds, availableMaps);
    }

    public MinigameData withMaxPlayers(int newMax) {
        return new MinigameData(mapId, displayName, newMax, minPlayers, rounds, availableMaps);
    }
}
