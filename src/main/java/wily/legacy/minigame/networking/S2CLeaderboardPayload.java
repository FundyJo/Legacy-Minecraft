package wily.legacy.minigame.networking;

import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.Legacy4J;

import java.util.UUID;

/**
 * S2C: Sends a leaderboard update to clients.
 */
public record S2CLeaderboardPayload(LeaderboardEntry[] entries) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CLeaderboardPayload> ID =
            CommonNetwork.Identifier.create(Legacy4J.createModLocation("leaderboard_update"), S2CLeaderboardPayload::decode);

    public static S2CLeaderboardPayload decode(CommonNetwork.PlayBuf buf) {
        int count = buf.get().readVarInt();
        LeaderboardEntry[] entries = new LeaderboardEntry[count];
        for (int i = 0; i < count; i++) {
            entries[i] = new LeaderboardEntry(
                    buf.get().readUUID(),
                    buf.get().readUtf(),
                    buf.get().readVarInt(),
                    buf.get().readVarInt()
            );
        }
        return new S2CLeaderboardPayload(entries);
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeVarInt(entries.length);
        for (LeaderboardEntry entry : entries) {
            buf.get().writeUUID(entry.playerId());
            buf.get().writeUtf(entry.playerName());
            buf.get().writeVarInt(entry.score());
            buf.get().writeVarInt(entry.rank());
        }
    }

    @Override
    public void apply(Context context) {
        if (wily.factoryapi.FactoryAPIPlatform.isClient()) {
            wily.legacy.minigame.client.MinigameClientState.updateLeaderboard(entries);
        }
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }

    public record LeaderboardEntry(UUID playerId, String playerName, int score, int rank) {}
}
