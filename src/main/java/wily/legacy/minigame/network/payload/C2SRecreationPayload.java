package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.minigame.controller.MinigamesController;
import wily.legacy.minigame.minigamedata.MinigameData;

public record C2SRecreationPayload(MinigameData data) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<C2SRecreationPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "c2srecreation"), C2SRecreationPayload::new);

    public C2SRecreationPayload(CommonNetwork.PlayBuf buf) {
        this(MinigameData.STREAM_CODEC.decode(buf.get()));
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        MinigameData.STREAM_CODEC.encode(buf.get(), data);
    }

    @Override
    public void apply(Context context) {
        if (context.player() == null) return;
        MinigamesController.getMinigameController(context.player().level()).playerRecreation(context.player(), data);
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
