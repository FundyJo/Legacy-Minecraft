package wily.legacy.minigame.config.battle;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * BLOCKED – SOURCE RECOVERY
 *
 * <p>The Battle minigame is absent from {@code Minimega-Project/minimega-decomp}; no original
 * source for {@code HungerSettings} could be recovered. The single constant {@code NORMAL} is a
 * Legacy4J placeholder and must <em>not</em> be treated as a verified 1:1 Minimega value.
 *
 * <p>Do not add further constants without confirmed Minimega source. When original source
 * becomes available, replace this enum in full and verify codec / stream-codec order.
 */
public enum HungerSettings implements StringRepresentable {
    /** UNVERIFIED PLACEHOLDER – source not recovered. */
    NORMAL("normal");

    public static final Codec<HungerSettings> CODEC = StringRepresentable.fromEnum(HungerSettings::values);
    public static final StreamCodec<ByteBuf, HungerSettings> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public HungerSettings decode(ByteBuf buffer) {
            return HungerSettings.values()[ByteBufCodecs.VAR_INT.decode(buffer)];
        }

        @Override
        public void encode(ByteBuf buffer, HungerSettings value) {
            ByteBufCodecs.VAR_INT.encode(buffer, value.ordinal());
        }
    };

    private final String name;

    HungerSettings(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
