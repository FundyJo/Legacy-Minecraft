package wily.legacy.minigame.grf.element;

import net.minecraft.resources.ResourceLocation;
import org.w3c.dom.Element;

/**
 * GRF element: ForcedBiome - overrides the biome rendering for client.
 */
public record ForcedBiome(ResourceLocation biomeId) implements GrfElement {

    public static ForcedBiome parse(Element element) {
        String biomeStr = element.getAttribute("id");
        if (biomeStr.isEmpty()) return new ForcedBiome(ResourceLocation.withDefaultNamespace("plains"));
        return new ForcedBiome(ResourceLocation.parse(biomeStr));
    }
}
