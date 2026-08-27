package wily.legacy.minigame.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record BattleVariants(List<MapVariant> variants) {
    public static final BattleVariants EMPTY = new BattleVariants(List.of());
    public static final Codec<BattleVariants> CODEC = MapVariant.CODEC.listOf().xmap(BattleVariants::new, BattleVariants::variants);

    public static final StreamCodec<ByteBuf, BattleVariants> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public BattleVariants decode(ByteBuf buffer) {
            return new BattleVariants(MapVariants.STREAM_CODEC.decode(buffer).variants());
        }

        @Override
        public void encode(ByteBuf buffer, BattleVariants value) {
            MapVariants.STREAM_CODEC.encode(buffer, new MapVariants(value.variants()));
        }
    };
}
