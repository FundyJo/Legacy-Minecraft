package wily.legacy.minigame.config.glide;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum GlideGameType implements StringRepresentable {
    TIME_ATTACK("time_attack"),
    SCORE_ATTACK("score_attack");

    private static final GlideGameType[] VALUES = values();

    public static final Codec<GlideGameType> CODEC = StringRepresentable.fromEnum(GlideGameType::values);
    public static final StreamCodec<ByteBuf, GlideGameType> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public GlideGameType decode(ByteBuf buffer) {
            int index = ByteBufCodecs.VAR_INT.decode(buffer);
            return ByIdMap.continuous(GlideGameType::ordinal, VALUES, ByIdMap.OutOfBoundsStrategy.ZERO).apply(index);
        }

        @Override
        public void encode(ByteBuf buffer, GlideGameType value) {
            ByteBufCodecs.VAR_INT.encode(buffer, value.ordinal());
        }
    };

    private final String name;

    GlideGameType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
