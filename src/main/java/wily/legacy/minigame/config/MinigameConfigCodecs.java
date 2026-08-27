package wily.legacy.minigame.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public final class MinigameConfigCodecs {
    private MinigameConfigCodecs() {
    }

    public static <T> Codec<T> interfaceCodec(List<Adapter<T>> adapters) {
        Codec<Adapter<T>> adapterCodec = Codec.STRING.comapFlatMap(
                id -> adapters.stream().filter(adapter -> adapter.id().equals(id)).findFirst()
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown type: " + id)),
                Adapter::id
        );
        return adapterCodec.dispatch("type", value -> findByValue(adapters, value), adapter -> castCodec(adapter.codec()));
    }

    public static <T> StreamCodec<ByteBuf, T> interfaceStreamCodec(List<Adapter<T>> adapters) {
        return new StreamCodec<>() {
            @Override
            public T decode(ByteBuf buffer) {
                String id = ByteBufCodecs.STRING_UTF8.decode(buffer);
                Adapter<T> adapter = adapters.stream()
                        .filter(entry -> entry.id().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Unknown type: " + id));
                return castStreamCodec(adapter.streamCodec()).decode(buffer);
            }

            @Override
            public void encode(ByteBuf buffer, T value) {
                Adapter<T> adapter = findByValue(adapters, value);
                ByteBufCodecs.STRING_UTF8.encode(buffer, adapter.id());
                castStreamCodec(adapter.streamCodec()).encode(buffer, value);
            }
        };
    }

    public record Adapter<T>(String id, Class<? extends T> type, Codec<? extends T> codec, StreamCodec<ByteBuf, ? extends T> streamCodec) {
    }

    private static <T> Adapter<T> findByValue(List<Adapter<T>> adapters, T value) {
        return adapters.stream()
                .filter(entry -> entry.type().isInstance(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported type: " + value.getClass().getName()));
    }

    @SuppressWarnings("unchecked")
    private static <T> Codec<T> castCodec(Codec<? extends T> codec) {
        return (Codec<T>) codec;
    }

    @SuppressWarnings("unchecked")
    private static <T> StreamCodec<ByteBuf, T> castStreamCodec(StreamCodec<ByteBuf, ? extends T> codec) {
        return (StreamCodec<ByteBuf, T>) codec;
    }
}
