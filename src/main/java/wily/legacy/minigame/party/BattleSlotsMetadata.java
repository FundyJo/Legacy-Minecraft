package wily.legacy.minigame.party;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record BattleSlotsMetadata() implements SlotsMetadata {
    public static final StreamCodec<ByteBuf, BattleSlotsMetadata> STREAM_CODEC = StreamCodec.unit(new BattleSlotsMetadata());
}
