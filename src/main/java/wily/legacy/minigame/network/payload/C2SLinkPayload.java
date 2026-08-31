package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record C2SLinkPayload(String code) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<C2SLinkPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "c2slink"), C2SLinkPayload::new);

    public C2SLinkPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readUtf(30));
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeUtf(code, 30);
    }

    @Override
    public void apply(Context context) {
        MinimegaNetworkHandlers.blockedPayload(this, "linking flow migration pending");
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
