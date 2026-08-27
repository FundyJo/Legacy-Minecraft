package wily.legacy.minigame.config.battle;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * Confirmed values: {@code BAT} and {@code INVISIBLE} are verified known constants
 * per issue specification. The complete set may be larger — the Battle minigame is
 * absent from {@code Minimega-Project/minimega-decomp}.
 *
 * <p>Do not add further constants without confirmed Minimega source.
 */
public enum SpectatorMode implements StringRepresentable {
    BAT("bat"),
    INVISIBLE("invisible");

    public static final Codec<SpectatorMode> CODEC = StringRepresentable.fromEnum(SpectatorMode::values);
    public static final StreamCodec<ByteBuf, SpectatorMode> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SpectatorMode decode(ByteBuf buffer) {
            return SpectatorMode.values()[ByteBufCodecs.VAR_INT.decode(buffer)];
        }

        @Override
        public void encode(ByteBuf buffer, SpectatorMode value) {
            ByteBufCodecs.VAR_INT.encode(buffer, value.ordinal());
        }
    };

    private final String name;

    SpectatorMode(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
