package wily.legacy.minigame.party;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LobbySlotMetadata(boolean ready) implements SlotMetadata {
    public static final StreamCodec<ByteBuf, LobbySlotMetadata> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, LobbySlotMetadata::ready,
            LobbySlotMetadata::new
    );
}
