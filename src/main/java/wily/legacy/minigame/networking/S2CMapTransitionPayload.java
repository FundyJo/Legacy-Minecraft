package wily.legacy.minigame.networking;

import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.Legacy4J;

/**
 * S2C: Notifies clients of a map transition (loading a new minigame map).
 */
public record S2CMapTransitionPayload(ResourceLocation mapId, String minigameName, int transitionDurationTicks) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CMapTransitionPayload> ID =
            CommonNetwork.Identifier.create(Legacy4J.createModLocation("map_transition"), S2CMapTransitionPayload::new);

    public S2CMapTransitionPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readResourceLocation(), buf.get().readUtf(), buf.get().readVarInt());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeResourceLocation(mapId);
        buf.get().writeUtf(minigameName);
        buf.get().writeVarInt(transitionDurationTicks);
    }

    @Override
    public void apply(Context context) {
        if (wily.factoryapi.FactoryAPIPlatform.isClient()) {
            wily.legacy.minigame.client.MinigameClientState.onMapTransition(mapId, minigameName, transitionDurationTicks);
        }
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
