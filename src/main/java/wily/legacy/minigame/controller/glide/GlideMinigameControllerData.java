package wily.legacy.minigame.controller.glide;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public final class GlideMinigameControllerData {
    private GlideMinigameControllerData() {
    }

    public record PlayerInformation(String playerName, int ordinal, Optional<Duration> finishTime, Optional<Integer> score) {
        private static final StreamCodec<ByteBuf, Duration> DURATION_STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.LONG, Duration::getSeconds,
                ByteBufCodecs.INT, Duration::getNano,
                Duration::ofSeconds
        );

        public static final StreamCodec<ByteBuf, PlayerInformation> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, PlayerInformation::playerName,
                ByteBufCodecs.VAR_INT, PlayerInformation::ordinal,
                ByteBufCodecs.optional(DURATION_STREAM_CODEC), PlayerInformation::finishTime,
                ByteBufCodecs.optional(ByteBufCodecs.INT), PlayerInformation::score,
                PlayerInformation::new
        );
    }

    public record Thermal(Identifier id, UUID owner, double x, double y, double z, int radius) {
        public static final StreamCodec<ByteBuf, Thermal> STREAM_CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC, Thermal::id,
                ByteBufCodecs.UUID, Thermal::owner,
                ByteBufCodecs.DOUBLE, Thermal::x,
                ByteBufCodecs.DOUBLE, Thermal::y,
                ByteBufCodecs.DOUBLE, Thermal::z,
                ByteBufCodecs.INT, Thermal::radius,
                Thermal::new
        );
    }
}
