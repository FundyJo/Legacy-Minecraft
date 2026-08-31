package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

import java.util.List;

public record S2CDownloadResourcePacksPayload(List<MinimegaPackObj> packs) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CDownloadResourcePacksPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "download_resource_packs"), S2CDownloadResourcePacksPayload::new);

    public S2CDownloadResourcePacksPayload(CommonNetwork.PlayBuf buf) {
        this(MinimegaPackObj.decodeList(buf));
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        MinimegaPackObj.encodeList(buf, packs);
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
