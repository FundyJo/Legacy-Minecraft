package wily.legacy.minigame.party;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import wily.legacy.minigame.controller.glide.GlideMinigameControllerData;

import java.util.Optional;

public record GlideSlotMetadata(int checkpoint,
                                double progressToNextCheckpoint,
                                boolean dead,
                                Optional<GlideMinigameControllerData.PlayerInformation> playerInformation) implements SlotMetadata {
    public static final StreamCodec<ByteBuf, GlideSlotMetadata> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, GlideSlotMetadata::checkpoint,
            ByteBufCodecs.DOUBLE, GlideSlotMetadata::progressToNextCheckpoint,
            ByteBufCodecs.BOOL, GlideSlotMetadata::dead,
            ByteBufCodecs.optional(GlideMinigameControllerData.PlayerInformation.STREAM_CODEC), GlideSlotMetadata::playerInformation,
            GlideSlotMetadata::new
    );
}
