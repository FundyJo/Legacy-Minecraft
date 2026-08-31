package wily.legacy.minigame.network.payload;

import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.minigame.controller.MinigamesController;

public final class MinimegaNetworkHandlers {
    private static final Logger LOGGER = LogManager.getLogger("Legacy4J/MinimegaNetwork");

    private MinimegaNetworkHandlers() {
    }

    public static void handleFinishedMapLoading(C2SFinishedMapLoadingPayload payload, CommonNetwork.Payload.Context context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MinigamesController.getMinigameController(player.level()).playerLoadedIn(player);
    }

    public static void handleReady(C2SReadyPayload payload, CommonNetwork.Payload.Context context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MinigamesController.getMinigameController(player.level()).playerReady(player, payload.ready());
    }

    public static void handleVote(C2SVotePayload payload, CommonNetwork.Payload.Context context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MinigamesController.getMinigameController(player.level()).playerVoted(player, payload.resourceLocation());
    }

    public static void handleRestart(C2SRestartPayload payload, CommonNetwork.Payload.Context context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MinigamesController.getMinigameController(player.level()).playerRestart(player, payload.fromStart());
    }

    public static void handleTimerSynchronization(C2STimerSynchronizationPayload payload, CommonNetwork.Payload.Context context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MinigamesController.getMinigameController(player.level()).playerTimerSynchronization(player, payload.number());
    }

    public static void handleTakeAll(C2STakeAllPayload payload, CommonNetwork.Payload.Context context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MinigamesController.getMinigameController(player.level()).playerTakeAll(player);
    }

    public static void blockedS2CMatchToSubmit(S2CMatchToSubmit payload, CommonNetwork.Payload.Context context) {
        LOGGER.warn("Received {} but matchmaking submit flow is blocked by client controller/UI migration", payload.identifier());
    }

    public static void blockedS2CPlayerPositions(S2CPlayerPositionsPayload payload, CommonNetwork.Payload.Context context) {
        LOGGER.warn("Received {} but glide HUD position flow is blocked by glide controller migration", payload.identifier());
    }

    public static void blockedS2CPlayerSlotObj(S2CPlayerSlotObjPayload payload, CommonNetwork.Payload.Context context) {
        LOGGER.warn("Received {} but player slot UI flow is blocked by minigame UI/controller migration", payload.identifier());
    }

    public static void blockedPayload(CommonNetwork.Payload payload, String reason) {
        LOGGER.warn("Received {} but handler is blocked: {}", payload.identifier(), reason);
    }
}
