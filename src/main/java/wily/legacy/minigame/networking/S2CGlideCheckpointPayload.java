package wily.legacy.minigame.networking;

import net.minecraft.core.BlockPos;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.Legacy4J;

/**
 * S2C: Notifies Glide players of checkpoint passage.
 */
public record S2CGlideCheckpointPayload(int checkpointIndex, int totalCheckpoints, long timeMs) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CGlideCheckpointPayload> ID =
            CommonNetwork.Identifier.create(Legacy4J.createModLocation("glide_checkpoint"), S2CGlideCheckpointPayload::new);

    public S2CGlideCheckpointPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readVarInt(), buf.get().readVarInt(), buf.get().readLong());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeVarInt(checkpointIndex);
        buf.get().writeVarInt(totalCheckpoints);
        buf.get().writeLong(timeMs);
    }

    @Override
    public void apply(Context context) {
        if (wily.factoryapi.FactoryAPIPlatform.isClient()) {
            wily.legacy.minigame.client.MinigameClientState.onGlideCheckpoint(checkpointIndex, totalCheckpoints, timeMs);
        }
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
