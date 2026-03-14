package wily.legacy.minigame.networking;

import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.Legacy4J;

/**
 * S2C: Sends a countdown notification to clients.
 */
public record S2CCountdownPayload(int ticksRemaining, boolean isStarting) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CCountdownPayload> ID =
            CommonNetwork.Identifier.create(Legacy4J.createModLocation("minigame_countdown"), S2CCountdownPayload::new);

    public S2CCountdownPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readVarInt(), buf.get().readBoolean());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeVarInt(ticksRemaining);
        buf.get().writeBoolean(isStarting);
    }

    @Override
    public void apply(Context context) {
        if (wily.factoryapi.FactoryAPIPlatform.isClient()) {
            wily.legacy.minigame.client.MinigameClientState.onCountdown(ticksRemaining, isStarting);
        }
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
