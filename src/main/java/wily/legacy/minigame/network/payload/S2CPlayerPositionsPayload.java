package wily.legacy.minigame.network.payload;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.minigame.controller.glide.GlideMinigameControllerData;

import java.util.List;

public record S2CPlayerPositionsPayload(List<GlideMinigameControllerData.PlayerInformation> playerInformations) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CPlayerPositionsPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "s2c_player_positions"), S2CPlayerPositionsPayload::new);

    private static final StreamCodec<io.netty.buffer.ByteBuf, List<GlideMinigameControllerData.PlayerInformation>> LIST_CODEC =
            ByteBufCodecs.list().apply(GlideMinigameControllerData.PlayerInformation.STREAM_CODEC);

    public S2CPlayerPositionsPayload(CommonNetwork.PlayBuf buf) {
        this(LIST_CODEC.decode(buf.get()));
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        LIST_CODEC.encode(buf.get(), playerInformations);
    }

    @Override
    public void apply(Context context) {
        MinimegaNetworkHandlers.blockedS2CPlayerPositions(this, context);
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
