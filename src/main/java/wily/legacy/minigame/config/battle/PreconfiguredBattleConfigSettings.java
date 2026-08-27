package wily.legacy.minigame.config.battle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PreconfiguredBattleConfigSettings(
        ItemSet itemSet,
        HungerSettings hungerSettings,
        Lives lives,
        SpectatorMode spectatorMode,
        RoundLength roundLength,
        MapSize mapSize
) implements BattleConfigSettings {
    public static final PreconfiguredBattleConfigSettings DEFAULT = new PreconfiguredBattleConfigSettings(
            ItemSet.NORMAL,
            HungerSettings.NORMAL,
            Lives.Infinite.INSTANCE,
            SpectatorMode.BAT,
            RoundLength.NORMAL,
            MapSize.AUTO
    );

    public static final Codec<PreconfiguredBattleConfigSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemSet.CODEC.optionalFieldOf("item_set", ItemSet.NORMAL).forGetter(PreconfiguredBattleConfigSettings::itemSet),
            HungerSettings.CODEC.optionalFieldOf("hunger", HungerSettings.NORMAL).forGetter(PreconfiguredBattleConfigSettings::hungerSettings),
            Lives.CODEC.optionalFieldOf("lives", Lives.Infinite.INSTANCE).forGetter(PreconfiguredBattleConfigSettings::lives),
            SpectatorMode.CODEC.optionalFieldOf("spectator_mode", SpectatorMode.BAT).forGetter(PreconfiguredBattleConfigSettings::spectatorMode),
            RoundLength.CODEC.optionalFieldOf("round_length", RoundLength.NORMAL).forGetter(PreconfiguredBattleConfigSettings::roundLength),
            MapSize.CODEC.optionalFieldOf("map_size", MapSize.AUTO).forGetter(PreconfiguredBattleConfigSettings::mapSize)
    ).apply(instance, PreconfiguredBattleConfigSettings::new));

    public static final StreamCodec<ByteBuf, PreconfiguredBattleConfigSettings> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PreconfiguredBattleConfigSettings decode(ByteBuf buffer) {
            return new PreconfiguredBattleConfigSettings(
                    ItemSet.STREAM_CODEC.decode(buffer),
                    HungerSettings.STREAM_CODEC.decode(buffer),
                    Lives.STREAM_CODEC.decode(buffer),
                    SpectatorMode.STREAM_CODEC.decode(buffer),
                    RoundLength.STREAM_CODEC.decode(buffer),
                    MapSize.STREAM_CODEC.decode(buffer)
            );
        }

        @Override
        public void encode(ByteBuf buffer, PreconfiguredBattleConfigSettings value) {
            ItemSet.STREAM_CODEC.encode(buffer, value.itemSet());
            HungerSettings.STREAM_CODEC.encode(buffer, value.hungerSettings());
            Lives.STREAM_CODEC.encode(buffer, value.lives());
            SpectatorMode.STREAM_CODEC.encode(buffer, value.spectatorMode());
            RoundLength.STREAM_CODEC.encode(buffer, value.roundLength());
            MapSize.STREAM_CODEC.encode(buffer, value.mapSize());
        }
    };
}
