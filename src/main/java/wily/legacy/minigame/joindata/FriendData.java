package wily.legacy.minigame.joindata;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import wily.legacy.minigame.Minigame;

import java.util.UUID;

public record FriendData(UUID friendUUID, Minigame minigame) implements CreateOrJoin {
    public static final StreamCodec<ByteBuf, FriendData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.UUID, FriendData::friendUUID,
            ByteBufCodecs.INT.map(Minigame::fromId, Minigame::getId), FriendData::minigame,
            FriendData::new
    );
}
