package wily.legacy.minigame.joindata;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import wily.legacy.minigame.minigamedata.MinigameData;

public record CreateParty(MinigameData data) implements CreateOrJoin {
    public static final StreamCodec<ByteBuf, CreateParty> STREAM_CODEC = StreamCodec.composite(
            MinigameData.STREAM_CODEC, CreateParty::data,
            CreateParty::new
    );
}
