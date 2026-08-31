package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record C2STimerSynchronizationPayload(int number) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<C2STimerSynchronizationPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "c2s_timer_synchronization"), C2STimerSynchronizationPayload::new);

    public C2STimerSynchronizationPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readInt());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeInt(number);
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
