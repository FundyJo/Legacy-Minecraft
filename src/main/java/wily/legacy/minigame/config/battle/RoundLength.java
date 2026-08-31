package wily.legacy.minigame.config.battle;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * BLOCKED – FUNDYJO/MINIMEGA SOURCE RECOVERY.
 *
 * <p>{@code FundyJo/Minimega} currently has
 * {@code dev/jab125/minimega/mod/util/minigamedata/battle/RoundLength.java} as
 * {@code // INTERNAL ERROR //}, so enum/value parity cannot be confirmed directly.
 */
public enum RoundLength implements StringRepresentable {
    NORMAL("normal");

    public static final Codec<RoundLength> CODEC = StringRepresentable.fromEnum(RoundLength::values);
    public static final StreamCodec<ByteBuf, RoundLength> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public RoundLength decode(ByteBuf buffer) {
            return RoundLength.values()[ByteBufCodecs.VAR_INT.decode(buffer)];
        }

        @Override
        public void encode(ByteBuf buffer, RoundLength value) {
            ByteBufCodecs.VAR_INT.encode(buffer, value.ordinal());
        }
    };

    private final String name;

    RoundLength(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
