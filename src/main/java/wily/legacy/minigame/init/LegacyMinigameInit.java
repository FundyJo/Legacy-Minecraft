package wily.legacy.minigame.init;

import wily.factoryapi.FactoryEvent;
import wily.legacy.Legacy4J;
import wily.legacy.minigame.controller.MinigamesController;

/**
 * Server-side initialization for the Legacy4J minigame system.
 * Registers server events for minigame management.
 * Network payloads are registered in Legacy4J's FactoryEvent.registerPayload block.
 * Storage is loaded per-server in MinigamesController.onServerStart.
 */
public class LegacyMinigameInit {

    public static void init() {
        Legacy4J.LOGGER.info("[Legacy4J Minigames] Initializing minigame system...");

        FactoryEvent.serverStarted(MinigamesController::onServerStart);
        FactoryEvent.serverStopped(MinigamesController::onServerStop);
        FactoryEvent.serverTick(MinigamesController::tickAll);

        Legacy4J.LOGGER.info("[Legacy4J Minigames] Minigame system initialized.");
    }
}
