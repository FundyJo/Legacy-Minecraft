package wily.legacy.minigame.joindata;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record Whatever() implements CreateOrJoin {
    public static final StreamCodec<ByteBuf, Whatever> STREAM_CODEC = StreamCodec.unit(new Whatever());
}
