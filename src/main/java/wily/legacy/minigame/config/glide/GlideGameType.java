package wily.legacy.minigame.config.glide;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Verified against Minimega-Project/minimega-decomp:
 * dev/jab125/minimega/util/controller/glide/GlideGameType.java
 *
 * Original values (ordinal order): TIME_ATTACK (0), SCORE_ATTACK (1)
 * Original CODEC: Codec.INT.xmap(a -> values()[a], Enum::ordinal) — integer-ordinal, not string-based.
 * STREAM_CODEC is a Legacy4J addition (not present in original); ordinal order preserved.
 */
public enum GlideGameType {
    TIME_ATTACK,
    SCORE_ATTACK;

    private static final GlideGameType[] VALUES = values();

    // Matches original: Codec.INT.xmap(a -> values()[a], Enum::ordinal)
    public static final Codec<GlideGameType> CODEC = Codec.INT.xmap(i -> VALUES[i], Enum::ordinal);

    // Legacy4J addition: stream codec for network use, ordinal order preserved.
    public static final StreamCodec<ByteBuf, GlideGameType> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public GlideGameType decode(ByteBuf buffer) {
            return VALUES[ByteBufCodecs.VAR_INT.decode(buffer)];
        }

        @Override
        public void encode(ByteBuf buffer, GlideGameType value) {
            ByteBufCodecs.VAR_INT.encode(buffer, value.ordinal());
        }
    };
}
