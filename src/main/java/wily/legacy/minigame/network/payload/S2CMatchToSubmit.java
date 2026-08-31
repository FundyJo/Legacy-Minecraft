package wily.legacy.minigame.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.minigame.p2p.matchmaking.obj.leaderboards.c2s.SubmitGlideMatchObj;

import java.time.Duration;

public record S2CMatchToSubmit(SubmitGlideMatchObj obj) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CMatchToSubmit> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "s2cmatchtosubmit"), S2CMatchToSubmit::new);

    private static final StreamCodec<ByteBuf, Duration> DURATION_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.LONG, Duration::getSeconds,
            ByteBufCodecs.INT, Duration::getNano,
            Duration::ofSeconds
    );

    private static final StreamCodec<ByteBuf, SubmitGlideMatchObj> INTERNAL_STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, SubmitGlideMatchObj::map,
            DURATION_STREAM_CODEC, SubmitGlideMatchObj::duration,
            ByteBufCodecs.optional(ByteBufCodecs.INT), SubmitGlideMatchObj::score,
            ByteBufCodecs.BOOL, SubmitGlideMatchObj::legacy4j,
            SubmitGlideMatchObj::new
    );

    public S2CMatchToSubmit(CommonNetwork.PlayBuf buf) {
        this(INTERNAL_STREAM_CODEC.decode(buf.get()));
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        INTERNAL_STREAM_CODEC.encode(buf.get(), obj);
    }

    @Override
    public void apply(Context context) {
        MinimegaNetworkHandlers.blockedS2CMatchToSubmit(this, context);
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
