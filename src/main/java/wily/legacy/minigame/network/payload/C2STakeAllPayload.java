package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record C2STakeAllPayload() implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<C2STakeAllPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "c2stakeall"), C2STakeAllPayload::new);

    public C2STakeAllPayload(CommonNetwork.PlayBuf buf) {
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
