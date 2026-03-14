package wily.legacy.minigame.networking;

import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.Legacy4J;

/**
 * S2C: Notifies clients of the current map vote tally.
 */
public record S2CVoteUpdatePayload(ResourceLocation[] maps, int[] voteCounts) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CVoteUpdatePayload> ID =
            CommonNetwork.Identifier.create(Legacy4J.createModLocation("vote_update"), S2CVoteUpdatePayload::decode);

    public static S2CVoteUpdatePayload decode(CommonNetwork.PlayBuf buf) {
        int count = buf.get().readVarInt();
        ResourceLocation[] maps = new ResourceLocation[count];
        int[] votes = new int[count];
        for (int i = 0; i < count; i++) {
            maps[i] = buf.get().readResourceLocation();
            votes[i] = buf.get().readVarInt();
        }
        return new S2CVoteUpdatePayload(maps, votes);
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeVarInt(maps.length);
        for (int i = 0; i < maps.length; i++) {
            buf.get().writeResourceLocation(maps[i]);
            buf.get().writeVarInt(voteCounts[i]);
        }
    }

    @Override
    public void apply(Context context) {
        if (wily.factoryapi.FactoryAPIPlatform.isClient()) {
            wily.legacy.minigame.client.MinigameClientState.onVoteUpdate(maps, voteCounts);
        }
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
