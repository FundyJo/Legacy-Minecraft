package wily.legacy.minigame.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import wily.factoryapi.base.network.CommonNetwork;

import java.util.ArrayList;
import java.util.List;

public record MinimegaPackObj(Identifier packId, String url, String hash, boolean required) {
    public static MinimegaPackObj decode(CommonNetwork.PlayBuf buf) {
        FriendlyByteBuf b = buf.get();
        return new MinimegaPackObj(b.readIdentifier(), b.readUtf(), b.readUtf(), b.readBoolean());
    }

    public static void encode(CommonNetwork.PlayBuf buf, MinimegaPackObj value) {
        FriendlyByteBuf b = buf.get();
        b.writeIdentifier(value.packId());
        b.writeUtf(value.url());
        b.writeUtf(value.hash());
        b.writeBoolean(value.required());
    }

    public static List<MinimegaPackObj> decodeList(CommonNetwork.PlayBuf buf) {
        return buf.get().readList(b -> new MinimegaPackObj(b.readIdentifier(), b.readUtf(), b.readUtf(), b.readBoolean()));
    }

    public static void encodeList(CommonNetwork.PlayBuf buf, List<MinimegaPackObj> values) {
        buf.get().writeCollection(values, (b, v) -> {
            b.writeIdentifier(v.packId());
            b.writeUtf(v.url());
            b.writeUtf(v.hash());
            b.writeBoolean(v.required());
        });
    }

    public static List<MinimegaPackObj> mutable(List<MinimegaPackObj> values) {
        return new ArrayList<>(values);
    }
}
