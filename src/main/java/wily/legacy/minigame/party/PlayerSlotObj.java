package wily.legacy.minigame.party;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PlayerSlotObj(int slotIndex, boolean active, boolean exists, boolean isMe, SlotMetadata metadata) {
    public static final StreamCodec<ByteBuf, PlayerSlotObj> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PlayerSlotObj::slotIndex,
            ByteBufCodecs.BOOL, PlayerSlotObj::active,
            ByteBufCodecs.BOOL, PlayerSlotObj::exists,
            ByteBufCodecs.BOOL, PlayerSlotObj::isMe,
            SlotMetadataCodecs.ofInterface(slotMetadata -> {
                        if (slotMetadata instanceof LobbySlotMetadata) return 0;
                        if (slotMetadata instanceof NoSlotMetadata) return 1;
                        if (slotMetadata instanceof GlideSlotMetadata) return 2;
                        throw new IllegalArgumentException("Unknown slot metadata type: " + slotMetadata.getClass().getName());
                    },
                    LobbySlotMetadata.STREAM_CODEC,
                    NoSlotMetadata.STREAM_CODEC,
                    GlideSlotMetadata.STREAM_CODEC),
            PlayerSlotObj::metadata,
            PlayerSlotObj::new
    );

    public PlayerSlotObj(PlayerSlotObj previous, boolean isMe) {
        this(previous.slotIndex, previous.active, previous.exists, isMe, previous.metadata);
    }
}
