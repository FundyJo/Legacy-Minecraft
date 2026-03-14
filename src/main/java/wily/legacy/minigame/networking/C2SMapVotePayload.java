package wily.legacy.minigame.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.Legacy4J;
import wily.legacy.minigame.controller.AbstractMinigameController;
import wily.legacy.minigame.controller.LobbyMinigameController;
import wily.legacy.minigame.controller.MinigamesController;

/**
 * C2S: Player votes for a map during lobby countdown.
 */
public record C2SMapVotePayload(ResourceLocation mapId) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<C2SMapVotePayload> ID =
            CommonNetwork.Identifier.create(Legacy4J.createModLocation("map_vote_c2s"), C2SMapVotePayload::new);

    public C2SMapVotePayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readResourceLocation());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeResourceLocation(mapId);
    }

    @Override
    public void apply(Context context) {
        if (context.player() instanceof ServerPlayer sp) {
            ServerLevel level = (ServerLevel) sp.level();
            AbstractMinigameController<?> controller = MinigamesController.getController(level);
            if (controller instanceof LobbyMinigameController lobbyController) {
                lobbyController.recordVote(sp, mapId);
            }
        }
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
