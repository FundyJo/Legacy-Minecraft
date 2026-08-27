package wily.legacy.minigame.config.battle;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import wily.legacy.minigame.config.MinigameConfigCodecs;

import java.util.List;

public sealed interface BattleConfigSettings permits PreconfiguredBattleConfigSettings, CasualBattleConfigSettings, CompetitiveBattleConfigSettings {
    BattleConfigSettings DEFAULT = new CasualBattleConfigSettings();

    Codec<BattleConfigSettings> CODEC = MinigameConfigCodecs.interfaceCodec(List.of(
            new MinigameConfigCodecs.Adapter<>("preconfigured", PreconfiguredBattleConfigSettings.class, PreconfiguredBattleConfigSettings.CODEC, PreconfiguredBattleConfigSettings.STREAM_CODEC),
            new MinigameConfigCodecs.Adapter<>("casual", CasualBattleConfigSettings.class, CasualBattleConfigSettings.CODEC, CasualBattleConfigSettings.STREAM_CODEC),
            new MinigameConfigCodecs.Adapter<>("competitive", CompetitiveBattleConfigSettings.class, CompetitiveBattleConfigSettings.CODEC, CompetitiveBattleConfigSettings.STREAM_CODEC)
    ));

    StreamCodec<ByteBuf, BattleConfigSettings> STREAM_CODEC = MinigameConfigCodecs.interfaceStreamCodec(List.of(
            new MinigameConfigCodecs.Adapter<>("preconfigured", PreconfiguredBattleConfigSettings.class, PreconfiguredBattleConfigSettings.CODEC, PreconfiguredBattleConfigSettings.STREAM_CODEC),
            new MinigameConfigCodecs.Adapter<>("casual", CasualBattleConfigSettings.class, CasualBattleConfigSettings.CODEC, CasualBattleConfigSettings.STREAM_CODEC),
            new MinigameConfigCodecs.Adapter<>("competitive", CompetitiveBattleConfigSettings.class, CompetitiveBattleConfigSettings.CODEC, CompetitiveBattleConfigSettings.STREAM_CODEC)
    ));
}
