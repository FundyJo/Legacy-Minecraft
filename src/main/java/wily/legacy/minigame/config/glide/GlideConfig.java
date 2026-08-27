package wily.legacy.minigame.config.glide;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import wily.legacy.minigame.config.MinigameSpecificConfig;

public record GlideConfig(GlideGameType gameType) implements MinigameSpecificConfig {
    public static final GlideConfig DEFAULT = new GlideConfig(GlideGameType.TIME_ATTACK);

    public static final Codec<GlideConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlideGameType.CODEC.optionalFieldOf("game_type", GlideGameType.TIME_ATTACK).forGetter(GlideConfig::gameType)
    ).apply(instance, GlideConfig::new));

    public static final StreamCodec<ByteBuf, GlideConfig> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public GlideConfig decode(ByteBuf buffer) {
            return new GlideConfig(GlideGameType.STREAM_CODEC.decode(buffer));
        }

        @Override
        public void encode(ByteBuf buffer, GlideConfig value) {
            GlideGameType.STREAM_CODEC.encode(buffer, value.gameType());
        }
    };
}
