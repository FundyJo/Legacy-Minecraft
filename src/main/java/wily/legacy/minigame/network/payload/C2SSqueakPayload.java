package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record C2SSqueakPayload() implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<C2SSqueakPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "c2ssqueak"), C2SSqueakPayload::new);

    public C2SSqueakPayload(CommonNetwork.PlayBuf buf) {
        this();
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
    }

    @Override
    public void apply(Context context) {
        MinimegaNetworkHandlers.blockedPayload(this, "squeak gameplay migration pending");
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
