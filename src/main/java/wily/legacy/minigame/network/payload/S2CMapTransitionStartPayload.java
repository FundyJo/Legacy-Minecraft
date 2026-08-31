package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.minigame.data.MapInfo;

public record S2CMapTransitionStartPayload(MapInfo info, boolean inInSameLevel) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CMapTransitionStartPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "map_transition_start"), S2CMapTransitionStartPayload::new);

    public S2CMapTransitionStartPayload(CommonNetwork.PlayBuf buf) {
        this(MapInfo.STREAM_CODEC.decode(buf.get()), buf.get().readBoolean());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        MapInfo.STREAM_CODEC.encode(buf.get(), info);
        buf.get().writeBoolean(inInSameLevel);
    }

    @Override
    public void apply(Context context) {
        MinimegaNetworkHandlers.blockedPayload(this, "client map transition migration pending");
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
