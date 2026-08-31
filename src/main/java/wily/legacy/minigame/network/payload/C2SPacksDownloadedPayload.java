package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record C2SPacksDownloadedPayload() implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<C2SPacksDownloadedPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "packs_downloaded"), C2SPacksDownloadedPayload::new);

    public C2SPacksDownloadedPayload(CommonNetwork.PlayBuf buf) {
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
