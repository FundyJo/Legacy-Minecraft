package wily.legacy.minigame.network.payload;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.minigame.controller.MinigamesController;
import wily.legacy.minigame.joindata.CreateOrJoin;
import wily.legacy.minigame.joindata.CreateOrJoinCodecs;

public record C2SJoiningChoicePayload(CreateOrJoin data) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<C2SJoiningChoicePayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "c2s_joining_choice"), C2SJoiningChoicePayload::new);

    public C2SJoiningChoicePayload(CommonNetwork.PlayBuf buf) {
        this(CreateOrJoinCodecs.STREAM_CODEC.decode(buf.get()));
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        CreateOrJoinCodecs.STREAM_CODEC.encode(buf.get(), data);
    }

    @Override
    public void apply(Context context) {
        if (context.player() == null) return;
        MinigamesController.getMinigameController(context.player().level()).playerJoiningChoice(context.player(), data);
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
