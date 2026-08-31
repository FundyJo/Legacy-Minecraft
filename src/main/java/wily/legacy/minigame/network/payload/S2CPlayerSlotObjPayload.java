package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.minigame.party.PlayerSlotObjs;

public record S2CPlayerSlotObjPayload(PlayerSlotObjs objs) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CPlayerSlotObjPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "s2c_playerslotobjspayload"), S2CPlayerSlotObjPayload::new);

    public S2CPlayerSlotObjPayload(CommonNetwork.PlayBuf buf) {
        this(PlayerSlotObjs.STREAM_CODEC.decode(buf.get()));
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        PlayerSlotObjs.STREAM_CODEC.encode(buf.get(), objs);
    }

    @Override
    public void apply(Context context) {
        MinimegaNetworkHandlers.blockedS2CPlayerSlotObj(this, context);
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
