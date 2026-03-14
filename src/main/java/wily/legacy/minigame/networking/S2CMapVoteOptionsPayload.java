package wily.legacy.minigame.networking;

import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.Legacy4J;

/**
 * S2C: Sends the list of available maps for voting to clients.
 */
public record S2CMapVoteOptionsPayload(ResourceLocation[] maps, String[] displayNames) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<S2CMapVoteOptionsPayload> ID =
            CommonNetwork.Identifier.create(Legacy4J.createModLocation("map_vote_options"), S2CMapVoteOptionsPayload::decode);

    public static S2CMapVoteOptionsPayload decode(CommonNetwork.PlayBuf buf) {
        int count = buf.get().readVarInt();
        ResourceLocation[] maps = new ResourceLocation[count];
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            maps[i] = buf.get().readResourceLocation();
            names[i] = buf.get().readUtf();
        }
        return new S2CMapVoteOptionsPayload(maps, names);
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeVarInt(maps.length);
        for (int i = 0; i < maps.length; i++) {
            buf.get().writeResourceLocation(maps[i]);
            buf.get().writeUtf(displayNames[i]);
        }
    }

    @Override
    public void apply(Context context) {
        if (wily.factoryapi.FactoryAPIPlatform.isClient()) {
            wily.legacy.minigame.client.MinigameClientState.onMapVoteOptions(maps, displayNames);
        }
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
