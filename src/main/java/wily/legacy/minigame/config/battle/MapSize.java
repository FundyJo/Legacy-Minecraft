package wily.legacy.minigame.config.battle;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * BLOCKED – SOURCE RECOVERY (partially confirmed)
 *
 * <p>{@code AUTO} is a confirmed known value per issue specification. However the complete
 * set of constants is unknown — the Battle minigame is absent from
 * {@code Minimega-Project/minimega-decomp} and no original source was recovered.
 *
 * <p>Do not add further constants without confirmed Minimega source. If additional values
 * exist (e.g. {@code SMALL}, {@code LARGE}), they must be recovered before adding.
 */
public enum MapSize implements StringRepresentable {
    /** Confirmed known value. Additional constants may exist – source not recovered. */
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
