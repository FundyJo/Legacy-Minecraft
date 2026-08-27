package wily.legacy.minigame.config.battle;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompetitiveBattleConfigSettings() implements BattleConfigSettings {
    public static final CompetitiveBattleConfigSettings INSTANCE = new CompetitiveBattleConfigSettings();
    public static final Codec<CompetitiveBattleConfigSettings> CODEC = Codec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, CompetitiveBattleConfigSettings> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public CompetitiveBattleConfigSettings decode(ByteBuf buffer) {
            return INSTANCE;
        }

        @Override
        public void encode(ByteBuf buffer, CompetitiveBattleConfigSettings value) {
        }
    };

    public PreconfiguredBattleConfigSettings asPreconfigured() {
        return PreconfiguredBattleConfigSettings.DEFAULT;
    }
}
