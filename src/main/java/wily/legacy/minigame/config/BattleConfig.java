package wily.legacy.minigame.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import wily.legacy.minigame.config.battle.BattleConfigSettings;

public record BattleConfig(BattleConfigSettings settings) implements MinigameSpecificConfig {
    public static final BattleConfig DEFAULT = new BattleConfig(BattleConfigSettings.DEFAULT);

    public static final Codec<BattleConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleConfigSettings.CODEC.optionalFieldOf("settings", BattleConfigSettings.DEFAULT).forGetter(BattleConfig::settings)
    ).apply(instance, BattleConfig::new));

    public static final StreamCodec<ByteBuf, BattleConfig> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public BattleConfig decode(ByteBuf buffer) {
            return new BattleConfig(BattleConfigSettings.STREAM_CODEC.decode(buffer));
        }

        @Override
        public void encode(ByteBuf buffer, BattleConfig value) {
            BattleConfigSettings.STREAM_CODEC.encode(buffer, value.settings());
        }
    };
}
