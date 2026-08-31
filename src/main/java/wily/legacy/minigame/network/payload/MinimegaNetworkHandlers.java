package wily.legacy.minigame.network.payload;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wily.factoryapi.base.network.CommonNetwork;

public final class MinimegaNetworkHandlers {
    private static final Logger LOGGER = LogManager.getLogger("Legacy4J/MinimegaNetwork");

    private MinimegaNetworkHandlers() {
    }

    public static void blocked(CommonNetwork.Payload payload, CommonNetwork.Payload.Context context) {
        LOGGER.warn("Received blocked Minimega payload {} from {} before controller migration is complete", payload.identifier(), context.player() != null ? context.player().getName().getString() : "unknown");
    }
}
