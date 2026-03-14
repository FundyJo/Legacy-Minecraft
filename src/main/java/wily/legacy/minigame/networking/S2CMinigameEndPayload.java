package wily.legacy.minigame.networking;

import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.Legacy4J;
import wily.legacy.minigame.Minigame;

import java.util.UUID;

/**
 * S2C: Notifies clients that a minigame has ended, with optional winner info.
 */
public record S2CMinigameEndPayload(String minigameName, boolean hasWinner, String winnerName) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CMinigameEndPayload> ID =
            CommonNetwork.Identifier.create(Legacy4J.createModLocation("minigame_end"), S2CMinigameEndPayload::new);

    public S2CMinigameEndPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readUtf(), buf.get().readBoolean(), buf.get().readUtf());
    }

    public static S2CMinigameEndPayload create(Minigame<?> minigame, UUID winner) {
        return new S2CMinigameEndPayload(minigame.getName(), winner != null, winner != null ? winner.toString() : "");
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeUtf(minigameName);
        buf.get().writeBoolean(hasWinner);
        buf.get().writeUtf(winnerName);
    }

    @Override
    public void apply(Context context) {
        if (wily.factoryapi.FactoryAPIPlatform.isClient()) {
            wily.legacy.minigame.client.MinigameClientState.onMinigameEnd(minigameName, hasWinner ? winnerName : null);
        }
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
