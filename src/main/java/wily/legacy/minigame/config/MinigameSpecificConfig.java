package wily.legacy.minigame.config;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import wily.legacy.minigame.config.glide.GlideConfig;

import java.util.List;

public sealed interface MinigameSpecificConfig permits NoConfig, BattleConfig, GlideConfig {
    Codec<MinigameSpecificConfig> CODEC = MinigameConfigCodecs.interfaceCodec(List.of(
            new MinigameConfigCodecs.Adapter<>("none", NoConfig.class, NoConfig.CODEC, NoConfig.STREAM_CODEC),
            new MinigameConfigCodecs.Adapter<>("battle", BattleConfig.class, BattleConfig.CODEC, BattleConfig.STREAM_CODEC),
            new MinigameConfigCodecs.Adapter<>("glide", GlideConfig.class, GlideConfig.CODEC, GlideConfig.STREAM_CODEC)
    ));

    StreamCodec<ByteBuf, MinigameSpecificConfig> STREAM_CODEC = MinigameConfigCodecs.interfaceStreamCodec(List.of(
            new MinigameConfigCodecs.Adapter<>("none", NoConfig.class, NoConfig.CODEC, NoConfig.STREAM_CODEC),
            new MinigameConfigCodecs.Adapter<>("battle", BattleConfig.class, BattleConfig.CODEC, BattleConfig.STREAM_CODEC),
            new MinigameConfigCodecs.Adapter<>("glide", GlideConfig.class, GlideConfig.CODEC, GlideConfig.STREAM_CODEC)
    ));
}
