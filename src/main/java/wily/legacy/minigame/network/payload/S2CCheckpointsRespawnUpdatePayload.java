package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record S2CCheckpointsRespawnUpdatePayload(int checkpoint, int respawnCheckpoint, boolean finishedMap, int score) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CCheckpointsRespawnUpdatePayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "s2crpup"), S2CCheckpointsRespawnUpdatePayload::new);

    public S2CCheckpointsRespawnUpdatePayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readInt(), buf.get().readInt(), buf.get().readBoolean(), buf.get().readVarInt());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeInt(checkpoint);
        buf.get().writeInt(respawnCheckpoint);
        buf.get().writeBoolean(finishedMap);
        buf.get().writeVarInt(score);
    }

    @Override
    public void apply(Context context) {
        MinimegaNetworkHandlers.blockedPayload(this, "client checkpoint HUD migration pending");
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
