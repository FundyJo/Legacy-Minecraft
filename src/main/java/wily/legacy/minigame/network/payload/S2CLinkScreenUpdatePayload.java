package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record S2CLinkScreenUpdatePayload(boolean successful) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CLinkScreenUpdatePayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "linkscreenpacket"), S2CLinkScreenUpdatePayload::new);

    public S2CLinkScreenUpdatePayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readBoolean());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeBoolean(successful);
    }

    @Override
    public void apply(Context context) {
        MinimegaNetworkHandlers.blockedPayload(this, "client linking UI migration pending");
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
