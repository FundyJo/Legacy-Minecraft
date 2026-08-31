package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.minigame.config.glide.GlideGameType;

import java.util.UUID;

public record S2CGlideFinishPayload(UUID playerUuid, int place, boolean bestResult, GlideGameType glideGameType) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CGlideFinishPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "s2c_glide_finish"), S2CGlideFinishPayload::new);

    public S2CGlideFinishPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readUUID(), buf.get().readVarInt(), buf.get().readBoolean(), GlideGameType.STREAM_CODEC.decode(buf.get()));
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeUUID(playerUuid);
        buf.get().writeVarInt(place);
        buf.get().writeBoolean(bestResult);
        GlideGameType.STREAM_CODEC.encode(buf.get(), glideGameType);
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
