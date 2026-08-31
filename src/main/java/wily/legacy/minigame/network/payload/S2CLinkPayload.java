package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record S2CLinkPayload(String code) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CLinkPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "s2clink"), S2CLinkPayload::new);

    public S2CLinkPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readUtf(30));
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeUtf(code, 30);
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
