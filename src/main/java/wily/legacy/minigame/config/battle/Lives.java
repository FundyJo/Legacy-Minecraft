package wily.legacy.minigame.config.battle;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public sealed interface Lives permits Lives.Infinite, Lives.Numbered {
    Codec<Lives> CODEC = Codec.INT.xmap(Lives::fromAmount, Lives::asAmount);

    StreamCodec<ByteBuf, Lives> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public Lives decode(ByteBuf buffer) {
            return Lives.fromAmount(ByteBufCodecs.VAR_INT.decode(buffer));
        }

        @Override
        public void encode(ByteBuf buffer, Lives value) {
            ByteBufCodecs.VAR_INT.encode(buffer, value.asAmount());
        }
    };

    int asAmount();

    static Lives fromAmount(int amount) {
        return amount <= 0 ? Infinite.INSTANCE : new Numbered(amount);
    }

    final class Infinite implements Lives {
        public static final Infinite INSTANCE = new Infinite();

        private Infinite() {
        }

        @Override
        public int asAmount() {
            return 0;
        }
    }

    record Numbered(int amount) implements Lives {
        public Numbered {
            if (amount <= 0) {
                throw new IllegalArgumentException("Numbered lives must be positive");
            }
        }

        @Override
        public int asAmount() {
            return amount;
        }
    }
}
