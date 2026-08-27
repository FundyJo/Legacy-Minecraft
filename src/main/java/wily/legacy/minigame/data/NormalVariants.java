package wily.legacy.minigame.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record NormalVariants(List<MapVariant> variants) {
    public static final NormalVariants EMPTY = new NormalVariants(List.of());
    public static final Codec<NormalVariants> CODEC = MapVariant.CODEC.listOf().xmap(NormalVariants::new, NormalVariants::variants);

    public static final StreamCodec<ByteBuf, NormalVariants> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public NormalVariants decode(ByteBuf buffer) {
            return new NormalVariants(MapVariants.STREAM_CODEC.decode(buffer).variants());
        }

        @Override
        public void encode(ByteBuf buffer, NormalVariants value) {
            MapVariants.STREAM_CODEC.encode(buffer, new MapVariants(value.variants()));
        }
    };
}
