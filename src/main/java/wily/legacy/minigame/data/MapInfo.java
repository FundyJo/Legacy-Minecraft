package wily.legacy.minigame.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import wily.legacy.minigame.Minigame;

import java.util.Optional;

public record MapInfo(ResourceLocation id, Minigame minigame, Optional<String> resourcepack, Optional<String> actualResourcePack) {
    public static final Codec<MapInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(MapInfo::id),
            Minigame.CODEC.fieldOf("minigame").forGetter(MapInfo::minigame),
            Codec.STRING.optionalFieldOf("resourcepack").forGetter(MapInfo::resourcepack),
            Codec.STRING.optionalFieldOf("actual_resource_pack").forGetter(MapInfo::actualResourcePack)
    ).apply(instance, MapInfo::new));

    public static final StreamCodec<ByteBuf, MapInfo> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public MapInfo decode(ByteBuf buffer) {
            MapVariant variant = MapVariant.STREAM_CODEC.decode(buffer);
            return new MapInfo(variant.id(), Minigame.STREAM_CODEC.decode(buffer), variant.resourcepack(), variant.actualResourcePack());
        }

        @Override
        public void encode(ByteBuf buffer, MapInfo value) {
            MapVariant.STREAM_CODEC.encode(buffer, new MapVariant(value.id(), value.resourcepack(), value.actualResourcePack()));
            Minigame.STREAM_CODEC.encode(buffer, value.minigame());
        }
    };

    public Component displayName() {
        return Component.translatable(id.getNamespace() + ".map." + minigame.tId() + "." + id.getPath());
    }

    public Component description() {
        return Component.translatable(id.getNamespace() + ".map." + minigame.tId() + "." + id.getPath() + ".description");
    }
}
