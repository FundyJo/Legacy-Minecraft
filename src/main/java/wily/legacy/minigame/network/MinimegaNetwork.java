package wily.legacy.minigame.network;

import wily.factoryapi.FactoryEvent;
import wily.legacy.minigame.controller.MinigamesController;
import wily.legacy.minigame.network.payload.*;

public final class MinimegaNetwork {
    private MinimegaNetwork() {
    }

    public static void register() {
        FactoryEvent.registerPayload(r -> {
            r.register(false, S2CJoiningChoicePayload.ID);
            r.register(true, C2SJoiningChoicePayload.ID);
            r.register(false, S2CLinkScreenUpdatePayload.ID);
            r.register(true, C2SLinkScreenClosedPayload.ID);
            r.register(false, S2CLinkPayload.ID);
            r.register(true, C2SLinkPayload.ID);
            r.register(true, C2SFinishedMapLoadingPayload.ID);
            r.register(false, S2CDisplayTextPayload.ID);
            r.register(false, S2CMapTransitionStartPayload.ID);
            r.register(false, S2CScoreRingCollisionPayload.ID);
            r.register(false, S2CGlideFinishPayload.ID);
            r.register(false, S2CTimerSynchronizationPayload.ID);
            r.register(false, S2CPlayerPositionsPayload.ID);
            r.register(false, S2CCheckpointsRespawnUpdatePayload.ID);
            r.register(false, S2CMatchToSubmit.ID);
            r.register(false, S2CPlayerSlotObjPayload.ID);
            r.register(false, S2CDisplayShieldPayload.ID);
            r.register(false, S2CGlobalSoundPayload.ID);
            r.register(true, C2SReadyPayload.ID);
            r.register(true, C2SVotePayload.ID);
            r.register(true, C2STimerSynchronizationPayload.ID);
            r.register(true, C2SRestartPayload.ID);
            r.register(true, C2STakeAllPayload.ID);
            r.register(true, C2SSqueakPayload.ID);
            r.register(true, C2SRecreationPayload.ID);
            r.register(false, C2S2CMinimegaProtocolVersionPayload.ID);
            r.register(false, S2CDownloadResourcePacksPayload.ID);
            r.register(true, C2S2CMinimegaProtocolVersionPayload.ID);
            r.register(true, C2SPacksDownloadedPayload.ID);
        });

        FactoryEvent.serverStarted(MinigamesController::onServerStarted);
        FactoryEvent.serverStopping(MinigamesController::onServerStopped);
        FactoryEvent.afterServerTick(MinigamesController::onServerTick);
        FactoryEvent.PlayerEvent.JOIN_EVENT.register(MinigamesController::onPlayerJoin);
    }
}
