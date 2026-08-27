package wily.legacy.minigame.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record MapVariant(ResourceLocation id, Optional<String> resourcepack, Optional<String> actualResourcePack) {
    public static final Codec<MapVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(MapVariant::id),
            Codec.STRING.optionalFieldOf("resourcepack").forGetter(MapVariant::resourcepack),
            Codec.STRING.optionalFieldOf("actual_resource_pack").forGetter(MapVariant::actualResourcePack)
    ).apply(instance, MapVariant::new));

    public static final StreamCodec<ByteBuf, MapVariant> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public MapVariant decode(ByteBuf buffer) {
            ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buffer);
            Optional<String> resourcePack = decodeOptionalString(buffer);
            Optional<String> actualResourcePack = decodeOptionalString(buffer);
            return new MapVariant(id, resourcePack, actualResourcePack);
        }

        @Override
        public void encode(ByteBuf buffer, MapVariant value) {
            ResourceLocation.STREAM_CODEC.encode(buffer, value.id());
            encodeOptionalString(buffer, value.resourcepack());
            encodeOptionalString(buffer, value.actualResourcePack());
        }

        private Optional<String> decodeOptionalString(ByteBuf buffer) {
            return buffer.readBoolean() ? Optional.of(ByteBufCodecs.STRING_UTF8.decode(buffer)) : Optional.empty();
        }

        private void encodeOptionalString(ByteBuf buffer, Optional<String> value) {
            buffer.writeBoolean(value.isPresent());
            value.ifPresent(v -> ByteBufCodecs.STRING_UTF8.encode(buffer, v));
        }
    };
}
