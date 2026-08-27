package wily.legacy.minigame.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record MapVariants(List<MapVariant> variants) {
    public static final MapVariants EMPTY = new MapVariants(List.of());
    public static final Codec<MapVariants> CODEC = MapVariant.CODEC.listOf().xmap(MapVariants::new, MapVariants::variants);

    public static final StreamCodec<ByteBuf, MapVariants> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public MapVariants decode(ByteBuf buffer) {
            int size = ByteBufCodecs.VAR_INT.decode(buffer);
            List<MapVariant> variants = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                variants.add(MapVariant.STREAM_CODEC.decode(buffer));
            }
            return new MapVariants(List.copyOf(variants));
        }

        @Override
        public void encode(ByteBuf buffer, MapVariants value) {
            ByteBufCodecs.VAR_INT.encode(buffer, value.variants().size());
            for (MapVariant variant : value.variants()) {
                MapVariant.STREAM_CODEC.encode(buffer, variant);
            }
        }
    };
}
