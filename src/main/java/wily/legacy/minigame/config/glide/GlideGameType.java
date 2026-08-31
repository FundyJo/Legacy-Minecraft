package wily.legacy.minigame.config.glide;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * NEEDS FUNDYJO/MINIMEGA VERIFICATION.
 *
 * <p>{@code FundyJo/Minimega} currently contains
 * {@code dev/jab125/minimega/mod/util/controller/glide/GlideGameType.java} as
 * {@code // INTERNAL ERROR //}, so enum/body parity cannot be directly confirmed.
 * Current constants and ordinal codecs are preserved as existing migration behavior
 * until source recovery is available inside {@code FundyJo/Minimega}.
 */
public enum GlideGameType {
    TIME_ATTACK,
    SCORE_ATTACK;

    private static final GlideGameType[] VALUES = values();

    public static final Codec<GlideGameType> CODEC = Codec.INT.xmap(i -> VALUES[i], Enum::ordinal);

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
