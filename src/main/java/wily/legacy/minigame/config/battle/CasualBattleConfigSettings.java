package wily.legacy.minigame.config.battle;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CasualBattleConfigSettings() implements BattleConfigSettings {
    public static final CasualBattleConfigSettings INSTANCE = new CasualBattleConfigSettings();
    public static final Codec<CasualBattleConfigSettings> CODEC = Codec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, CasualBattleConfigSettings> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public CasualBattleConfigSettings decode(ByteBuf buffer) {
            return INSTANCE;
        }

        @Override
        public void encode(ByteBuf buffer, CasualBattleConfigSettings value) {
        }
    };

    public PreconfiguredBattleConfigSettings asPreconfigured() {
        return PreconfiguredBattleConfigSettings.DEFAULT;
    }
}
