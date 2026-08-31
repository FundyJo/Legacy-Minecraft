package wily.legacy.minigame.party;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record GlideSlotsMetadata(List<ReducedCheckpoint> checkpointInfo, int round, int maxRounds) implements SlotsMetadata {
    public static final StreamCodec<ByteBuf, GlideSlotsMetadata> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.list().apply(ReducedCheckpoint.STREAM_CODEC), GlideSlotsMetadata::checkpointInfo,
            ByteBufCodecs.INT, GlideSlotsMetadata::round,
            ByteBufCodecs.INT, GlideSlotsMetadata::maxRounds,
            GlideSlotsMetadata::new
    );
}
