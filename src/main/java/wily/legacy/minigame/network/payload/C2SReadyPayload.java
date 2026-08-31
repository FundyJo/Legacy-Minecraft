package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record C2SReadyPayload(boolean ready) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<C2SReadyPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "ready"), C2SReadyPayload::new);

    public C2SReadyPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readBoolean());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeBoolean(ready);
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
