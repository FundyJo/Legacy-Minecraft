package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record C2SRestartPayload(boolean fromStart) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<C2SRestartPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "c2srestart"), C2SRestartPayload::new);

    public C2SRestartPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readBoolean());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeBoolean(fromStart);
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
