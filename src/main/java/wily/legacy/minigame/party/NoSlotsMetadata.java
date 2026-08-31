package wily.legacy.minigame.party;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NoSlotsMetadata() implements SlotsMetadata {
    public static final StreamCodec<ByteBuf, NoSlotsMetadata> STREAM_CODEC = StreamCodec.unit(new NoSlotsMetadata());
}
