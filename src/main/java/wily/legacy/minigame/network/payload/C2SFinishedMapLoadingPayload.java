package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record C2SFinishedMapLoadingPayload() implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<C2SFinishedMapLoadingPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "finished_map_loading"), C2SFinishedMapLoadingPayload::new);

    public C2SFinishedMapLoadingPayload(CommonNetwork.PlayBuf buf) {
        this();
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
    }

    @Override
    public void apply(Context context) {
        MinimegaNetworkHandlers.handleFinishedMapLoading(this, context);
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
