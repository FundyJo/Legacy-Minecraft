package wily.legacy.minigame.networking;

import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.Legacy4J;
import wily.legacy.minigame.Minigame;

import java.util.UUID;

/**
 * S2C: Notifies clients that a minigame has started.
 */
public record S2CMinigameStartPayload(String minigameName, ResourceLocation mapId, int maxPlayers) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CMinigameStartPayload> ID =
            CommonNetwork.Identifier.create(Legacy4J.createModLocation("minigame_start"), S2CMinigameStartPayload::new);

    public S2CMinigameStartPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readUtf(), buf.get().readResourceLocation(), buf.get().readVarInt());
    }

    public static S2CMinigameStartPayload create(Minigame<?> minigame, ResourceLocation mapId, int maxPlayers) {
        return new S2CMinigameStartPayload(minigame.getName(), mapId, maxPlayers);
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeUtf(minigameName);
        buf.get().writeResourceLocation(mapId);
        buf.get().writeVarInt(maxPlayers);
    }

    @Override
    public void apply(Context context) {
        if (wily.factoryapi.FactoryAPIPlatform.isClient()) {
            wily.legacy.minigame.client.MinigameClientState.onMinigameStart(minigameName, mapId, maxPlayers);
        }
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
