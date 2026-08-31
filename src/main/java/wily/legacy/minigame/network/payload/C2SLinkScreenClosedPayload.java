package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record C2SLinkScreenClosedPayload() implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<C2SLinkScreenClosedPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "link_screen_closed"), C2SLinkScreenClosedPayload::new);

    public C2SLinkScreenClosedPayload(CommonNetwork.PlayBuf buf) {
        this();
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
    }

    @Override
    public void apply(Context context) {
        MinimegaNetworkHandlers.blocked(this, context);
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
