package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

import java.util.UUID;

public record S2CScoreRingCollisionPayload(int level, UUID uuid, int points) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CScoreRingCollisionPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "score_ring_collision"), S2CScoreRingCollisionPayload::new);

    public S2CScoreRingCollisionPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readInt(), buf.get().readUUID(), buf.get().readVarInt());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeInt(level);
        buf.get().writeUUID(uuid);
        buf.get().writeVarInt(points);
    }

    @Override
    public void apply(Context context) {
        MinimegaNetworkHandlers.blockedPayload(this, "client glide ring collision migration pending");
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
