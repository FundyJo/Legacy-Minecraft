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
 * source for {@code ItemSet} could be recovered. The single constant {@code NORMAL} is a
 * Legacy4J placeholder and must <em>not</em> be treated as a verified 1:1 Minimega value.
 *
 * <p>Do not add further constants without confirmed Minimega source. When original source
 * becomes available, replace this enum in full and verify codec / stream-codec order.
 */
public enum ItemSet implements StringRepresentable {
    /** UNVERIFIED PLACEHOLDER – source not recovered. */
    NORMAL("normal");

    public static final Codec<ItemSet> CODEC = StringRepresentable.fromEnum(ItemSet::values);
    public static final StreamCodec<ByteBuf, ItemSet> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ItemSet decode(ByteBuf buffer) {
            return ItemSet.values()[ByteBufCodecs.VAR_INT.decode(buffer)];
        }

        @Override
        public void encode(ByteBuf buffer, ItemSet value) {
            ByteBufCodecs.VAR_INT.encode(buffer, value.ordinal());
        }
    };

    private final String name;

    ItemSet(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
