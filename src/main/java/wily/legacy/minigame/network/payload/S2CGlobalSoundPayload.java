package wily.legacy.minigame.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

import java.util.Optional;

public record S2CGlobalSoundPayload(Identifier id, Optional<BlockPos> pos) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CGlobalSoundPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "s2c_global_sound"), S2CGlobalSoundPayload::new);

    public S2CGlobalSoundPayload(Identifier id) {
        this(id, Optional.empty());
    }

    public S2CGlobalSoundPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readIdentifier(), buf.get().readOptional(b -> b.readBlockPos()));
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeIdentifier(id);
        buf.get().writeOptional(pos, (b, p) -> b.writeBlockPos(p));
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
