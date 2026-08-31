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
 * {@code dev/jab125/minimega/mod/util/minigamedata/battle/ItemSet.java} as
 * {@code // INTERNAL ERROR //}, so enum/value parity cannot be confirmed directly.
 */
public enum ItemSet implements StringRepresentable {
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
