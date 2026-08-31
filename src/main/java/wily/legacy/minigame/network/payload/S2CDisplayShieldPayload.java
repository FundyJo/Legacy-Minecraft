package wily.legacy.minigame.network.payload;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record S2CDisplayShieldPayload(Identifier sprite, Component component, int priority) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CDisplayShieldPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "s2cdisplayshield"), S2CDisplayShieldPayload::new);

    public S2CDisplayShieldPayload(Identifier sprite, Component component) {
        this(sprite, component, 0);
    }

    public S2CDisplayShieldPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readIdentifier(), CommonNetwork.decodeComponent(buf), buf.get().readVarInt());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeIdentifier(sprite);
        CommonNetwork.encodeComponent(buf, component);
        buf.get().writeVarInt(priority);
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
