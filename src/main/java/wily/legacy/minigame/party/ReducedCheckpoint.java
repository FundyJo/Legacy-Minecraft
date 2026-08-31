package wily.legacy.minigame.party;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ReducedCheckpoint(int id, boolean respawn) {
    public static final StreamCodec<ByteBuf, ReducedCheckpoint> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ReducedCheckpoint::id,
            ByteBufCodecs.BOOL, ReducedCheckpoint::respawn,
            ReducedCheckpoint::new
    );
}
