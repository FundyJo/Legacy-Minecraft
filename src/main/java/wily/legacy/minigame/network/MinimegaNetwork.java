package wily.legacy.minigame.network;

import wily.factoryapi.FactoryEvent;

public final class MinimegaNetwork {
    private MinimegaNetwork() {
    }

    public static void register() {
        FactoryEvent.registerPayload(r -> {
            // Payload activation is deferred until controller migrations provide parity-safe handlers.
        });
    }
}
