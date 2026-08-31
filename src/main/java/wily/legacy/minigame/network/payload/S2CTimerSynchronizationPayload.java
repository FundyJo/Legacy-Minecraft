package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

import java.time.Duration;

public record S2CTimerSynchronizationPayload(Duration duration, int number, boolean leaderboardCounted) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CTimerSynchronizationPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "s2c_timer_synchronization"), S2CTimerSynchronizationPayload::new);

    public S2CTimerSynchronizationPayload(CommonNetwork.PlayBuf buf) {
        this(Duration.ofSeconds(buf.get().readLong(), buf.get().readInt()), buf.get().readInt(), buf.get().readBoolean());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeLong(duration.getSeconds());
        buf.get().writeInt(duration.getNano());
        buf.get().writeInt(number);
        buf.get().writeBoolean(leaderboardCounted);
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
