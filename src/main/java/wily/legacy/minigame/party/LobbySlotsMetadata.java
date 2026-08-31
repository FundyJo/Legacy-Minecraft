package wily.legacy.minigame.party;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record LobbySlotsMetadata() implements SlotsMetadata {
    public static final StreamCodec<ByteBuf, LobbySlotsMetadata> STREAM_CODEC = StreamCodec.unit(new LobbySlotsMetadata());
}
