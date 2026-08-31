package wily.legacy.minigame.network.payload;

import net.minecraft.resources.Identifier;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record C2SVotePayload(Identifier resourceLocation) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<C2SVotePayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "vote"), C2SVotePayload::new);

    public C2SVotePayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readIdentifier());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeIdentifier(resourceLocation);
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
