package wily.legacy.minigame.config.battle;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum MapSize implements StringRepresentable {
    AUTO("auto");

    public static final Codec<MapSize> CODEC = StringRepresentable.fromEnum(MapSize::values);
    public static final StreamCodec<ByteBuf, MapSize> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public MapSize decode(ByteBuf buffer) {
            return MapSize.values()[ByteBufCodecs.VAR_INT.decode(buffer)];
        }

        @Override
        public void encode(ByteBuf buffer, MapSize value) {
            ByteBufCodecs.VAR_INT.encode(buffer, value.ordinal());
        }
    };

    private final String name;

    MapSize(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
