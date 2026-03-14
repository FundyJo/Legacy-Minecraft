package wily.legacy.minigame.controller;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import wily.legacy.minigame.Minigame;
import wily.legacy.minigame.MinigameData;
import wily.legacy.minigame.networking.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Base class for all minigame controllers.
 * Each controller manages one active game session in a given ServerLevel.
 */
public abstract class AbstractMinigameController<T extends AbstractMinigameController<T>> {

    protected ServerLevel level;
    protected MinigameData data;
    protected boolean active = false;
    protected int tick = 0;
    protected final List<UUID> players = new ArrayList<>();
    protected int countdownTicks = -1;

    protected AbstractMinigameController(ServerLevel level) {
        this.level = level;
    }

    public void start(MinigameData data) {
        this.data = data;
        this.active = true;
        this.tick = 0;
        this.players.clear();
        onStart();
    }

    public void end() {
        this.active = false;
        onEnd();
        broadcastToPlayers(S2CMinigameEndPayload.create(getMinigame(), getWinner()));
    }

    public void tick(ServerLevel level) {
        if (!active) return;
        this.level = level;
        this.tick++;
        if (countdownTicks > 0) {
            countdownTicks--;
            if (countdownTicks == 0) {
                onCountdownEnd();
            }
        }
        onTick();
    }

    protected abstract void onStart();
    protected abstract void onEnd();
    protected abstract void onTick();
    protected abstract Minigame<T> getMinigame();

    protected void onCountdownEnd() {}

    protected UUID getWinner() {
        return null;
    }

    public boolean isActive() {
        return active;
    }

    public MinigameData getData() {
        return data;
    }

    public List<UUID> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public void addPlayer(ServerPlayer player) {
        if (!players.contains(player.getUUID())) {
            players.add(player.getUUID());
            onPlayerJoin(player);
        }
    }

    public void removePlayer(UUID playerUUID) {
        players.remove(playerUUID);
        ServerPlayer player = level.getServer().getPlayerList().getPlayer(playerUUID);
        if (player != null) onPlayerLeave(player);
    }

    protected void onPlayerJoin(ServerPlayer player) {}
    protected void onPlayerLeave(ServerPlayer player) {}

    protected void broadcastToPlayers(wily.factoryapi.base.network.CommonNetwork.Payload payload) {
        for (UUID uuid : players) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
            if (player != null) {
                wily.factoryapi.base.network.CommonNetwork.sendToPlayer(player, payload);
            }
        }
    }

    /** Broadcasts a payload to ALL players currently on the server (e.g. for map transition announcements). */
    protected void broadcastToAllPlayers(wily.factoryapi.base.network.CommonNetwork.Payload payload) {
        wily.factoryapi.base.network.CommonNetwork.sendToPlayers(
                level.getServer().getPlayerList().getPlayers(), payload);
    }

    protected void broadcastTitle(Component title, Component subtitle) {
        for (UUID uuid : players) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
            if (player != null) {
                sendTitle(player, title, subtitle);
            }
        }
    }

    protected void sendTitle(ServerPlayer player, Component title, Component subtitle) {
        player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket(10, 70, 20));
        player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(title));
        if (subtitle != null && !subtitle.getString().isEmpty()) {
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket(subtitle));
        }
    }

    protected void startCountdown(int ticks) {
        this.countdownTicks = ticks;
    }

    public ServerLevel getLevel() {
        return level;
    }
}
