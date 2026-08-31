package wily.legacy.minigame.party;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NoSlotMetadata() implements SlotMetadata {
    public static final StreamCodec<ByteBuf, NoSlotMetadata> STREAM_CODEC = StreamCodec.unit(new NoSlotMetadata());
}
