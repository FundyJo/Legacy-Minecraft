package wily.legacy.minigame.party;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record PlayerSlotObjs(List<PlayerSlotObj> list, SlotsMetadata slotsMetadata) {
    public static final StreamCodec<ByteBuf, PlayerSlotObjs> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.list().apply(PlayerSlotObj.STREAM_CODEC), PlayerSlotObjs::list,
            SlotsMetadataCodecs.STREAM_CODEC, PlayerSlotObjs::slotsMetadata,
            PlayerSlotObjs::new
    );
}
