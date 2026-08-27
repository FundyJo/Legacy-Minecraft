package wily.legacy.minigame.config;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class NoConfig implements MinigameSpecificConfig {
    public static final NoConfig INSTANCE = new NoConfig();
    public static final Codec<NoConfig> CODEC = Codec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, NoConfig> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public NoConfig decode(ByteBuf buffer) {
            return INSTANCE;
        }

        @Override
        public void encode(ByteBuf buffer, NoConfig value) {
        }
    };

    private NoConfig() {
    }
}
