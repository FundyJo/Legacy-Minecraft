package wily.legacy.minigame.joindata;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import wily.legacy.minigame.party.SlotMetadataCodecs;

public final class CreateOrJoinCodecs {
    private CreateOrJoinCodecs() {
    }

    public static final StreamCodec<ByteBuf, CreateOrJoin> STREAM_CODEC = SlotMetadataCodecs.ofInterface(createOrJoin -> {
                if (createOrJoin instanceof CreateParty) return 0;
                if (createOrJoin instanceof FriendData) return 1;
                if (createOrJoin instanceof JoinParty) return 2;
                if (createOrJoin instanceof Whatever) return 3;
                throw new IllegalArgumentException("Unknown createOrJoin type: " + createOrJoin.getClass().getName());
            },
            CreateParty.STREAM_CODEC,
            FriendData.STREAM_CODEC,
            JoinParty.STREAM_CODEC,
            Whatever.STREAM_CODEC
    );
}
