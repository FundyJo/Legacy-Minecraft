package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record S2CJoiningChoicePayload() implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CJoiningChoicePayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "s2c_joining_choice"), S2CJoiningChoicePayload::new);

    public S2CJoiningChoicePayload(CommonNetwork.PlayBuf buf) {
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
