package wily.legacy.minigame.network.payload;

import net.minecraft.network.chat.Component;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record S2CDisplayTextPayload(Component component) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CDisplayTextPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "display_text"), S2CDisplayTextPayload::new);

    public S2CDisplayTextPayload(CommonNetwork.PlayBuf buf) {
        this(CommonNetwork.decodeComponent(buf));
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        CommonNetwork.encodeComponent(buf, component);
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
