package wily.legacy.minigame.party;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class SlotsMetadataCodecs {
    private SlotsMetadataCodecs() {
    }

    public static final StreamCodec<ByteBuf, SlotsMetadata> STREAM_CODEC = SlotMetadataCodecs.ofInterface(slotsMetadata -> {
                if (slotsMetadata instanceof BattleSlotsMetadata) return 0;
                if (slotsMetadata instanceof LobbySlotsMetadata) return 1;
                if (slotsMetadata instanceof NoSlotsMetadata) return 2;
                if (slotsMetadata instanceof GlideSlotsMetadata) return 3;
                throw new IllegalArgumentException("Unknown slots metadata type: " + slotsMetadata.getClass().getName());
            },
            BattleSlotsMetadata.STREAM_CODEC,
            LobbySlotsMetadata.STREAM_CODEC,
            NoSlotsMetadata.STREAM_CODEC,
            GlideSlotsMetadata.STREAM_CODEC
    );
}
