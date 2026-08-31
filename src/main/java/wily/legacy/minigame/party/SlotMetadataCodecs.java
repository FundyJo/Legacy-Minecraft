package wily.legacy.minigame.party;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Map;
import java.util.function.Function;

public final class SlotMetadataCodecs {
    private SlotMetadataCodecs() {
    }

    @SafeVarargs
    public static <T> StreamCodec<ByteBuf, T> ofInterface(Function<T, Integer> typeResolver, StreamCodec<? super ByteBuf, ? extends T>... codecs) {
        return new StreamCodec<>() {
            @Override
            public T decode(ByteBuf input) {
                int type = ByteBufCodecs.VAR_INT.decode(input);
                return castStreamCodec(codecs[type]).decode(input);
            }

            @Override
            public void encode(ByteBuf output, T value) {
                int type = typeResolver.apply(value);
                ByteBufCodecs.VAR_INT.encode(output, type);
                castStreamCodec(codecs[type]).encode(output, value);
            }
        };
    }

    @SafeVarargs
    public static <T> Codec<T> ofInterface(Function<T, Integer> typeResolver, Codec<? extends T>... codecs) {
        return new Codec<>() {
            @Override
            public <X> DataResult<Pair<T, X>> decode(DynamicOps<X> ops, X input) {
                return ops.getMap(input).flatMap(map -> ops.getNumberValue(map.get("type"))
                        .flatMap(type -> castCodec(codecs[type.intValue()]).decode(ops, map.get("value")))
                        .map(result -> Pair.of(result.getFirst(), input)));
            }

            @Override
            public <X> DataResult<X> encode(T input, DynamicOps<X> ops, X prefix) {
                int type = typeResolver.apply(input);
                return castCodec(codecs[type]).encode(input, ops, prefix)
                        .map(value -> ops.createMap(Map.of(
                                ops.createString("type"), ops.createInt(type),
                                ops.createString("value"), value
                        )));
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> Codec<T> castCodec(Codec<? extends T> codec) {
        return (Codec<T>) codec;
    }

    @SuppressWarnings("unchecked")
    private static <T> StreamCodec<ByteBuf, T> castStreamCodec(StreamCodec<? super ByteBuf, ? extends T> codec) {
        return (StreamCodec<ByteBuf, T>) codec;
    }
}
