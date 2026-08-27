package wily.legacy.minigame.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MapData(NormalVariants normal, BattleVariants battle) {
    public static final MapData EMPTY = new MapData(NormalVariants.EMPTY, BattleVariants.EMPTY);

    public static final Codec<MapData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NormalVariants.CODEC.optionalFieldOf("normal", NormalVariants.EMPTY).forGetter(MapData::normal),
            BattleVariants.CODEC.optionalFieldOf("battle", BattleVariants.EMPTY).forGetter(MapData::battle)
    ).apply(instance, MapData::new));

    public static final StreamCodec<ByteBuf, MapData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public MapData decode(ByteBuf buffer) {
            return new MapData(NormalVariants.STREAM_CODEC.decode(buffer), BattleVariants.STREAM_CODEC.decode(buffer));
        }

        @Override
        public void encode(ByteBuf buffer, MapData value) {
            NormalVariants.STREAM_CODEC.encode(buffer, value.normal());
            BattleVariants.STREAM_CODEC.encode(buffer, value.battle());
        }
    };
}
