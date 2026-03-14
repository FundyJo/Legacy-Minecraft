package wily.legacy.minigame.networking;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.Legacy4J;
import wily.legacy.minigame.Minigame;
import wily.legacy.minigame.MinigameData;
import wily.legacy.minigame.controller.MinigamesController;

/**
 * C2S: Player requests to join or start a minigame.
 */
public record C2SJoinMinigamePayload(String minigameName, int maxPlayers, int rounds) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<C2SJoinMinigamePayload> ID =
            CommonNetwork.Identifier.create(Legacy4J.createModLocation("join_minigame_c2s"), C2SJoinMinigamePayload::new);

    public C2SJoinMinigamePayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readUtf(), buf.get().readVarInt(), buf.get().readVarInt());
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeUtf(minigameName);
        buf.get().writeVarInt(maxPlayers);
        buf.get().writeVarInt(rounds);
    }

    @Override
    public void apply(Context context) {
        if (context.player() instanceof ServerPlayer sp) {
            ServerLevel level = (ServerLevel) sp.level();
            Minigame.fromName(minigameName).ifPresent(minigame -> {
                MinigameData data = new MinigameData(
                        wily.legacy.Legacy4J.createModLocation("default"),
                        minigameName,
                        maxPlayers,
                        2,
                        rounds,
                        java.util.List.of()
                );
                MinigamesController.startMinigame(level, minigame, data);
            });
        }
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
