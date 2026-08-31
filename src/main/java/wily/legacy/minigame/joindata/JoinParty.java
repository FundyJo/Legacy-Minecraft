package wily.legacy.minigame.joindata;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import wily.legacy.minigame.Minigame;

public record JoinParty(Minigame minigame) implements CreateOrJoin {
    public static final StreamCodec<ByteBuf, JoinParty> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT.map(Minigame::fromId, Minigame::getId), JoinParty::minigame,
            JoinParty::new
    );
}
