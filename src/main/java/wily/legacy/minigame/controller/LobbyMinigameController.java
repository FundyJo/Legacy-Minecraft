package wily.legacy.minigame.controller;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import wily.legacy.Legacy4J;
import wily.legacy.minigame.Minigame;
import wily.legacy.minigame.networking.S2CCountdownPayload;
import wily.legacy.minigame.networking.S2CMapVoteOptionsPayload;
import wily.legacy.minigame.networking.S2CVoteUpdatePayload;

import java.util.*;

/**
 * Lobby controller: manages player gathering, map voting, and game dispatch.
 * Players gather, vote on a map, then the winning minigame/map is launched.
 */
public class LobbyMinigameController extends AbstractMinigameController<LobbyMinigameController> {

    private static final int LOBBY_WAIT_TICKS = 20 * 30;
    private static final int COUNTDOWN_TICKS = 20 * 10;
    private static final int VOTE_TICKS = 20 * 20;

    private final Map<UUID, ResourceLocation> votes = new HashMap<>();
    private ResourceLocation[] voteOptions = new ResourceLocation[0];
    private String[] voteOptionNames = new String[0];
    private LobbyPhase phase = LobbyPhase.WAITING;
    private int phaseTimer = 0;

    public enum LobbyPhase { WAITING, VOTING, COUNTDOWN, DISPATCHING }

    public LobbyMinigameController(ServerLevel level) {
        super(level);
    }

    @Override
    protected void onStart() {
        phase = LobbyPhase.WAITING;
        phaseTimer = 0;
        votes.clear();
        if (data.availableMaps() != null && !data.availableMaps().isEmpty()) {
            voteOptions = data.availableMaps().toArray(new ResourceLocation[0]);
            voteOptionNames = new String[voteOptions.length];
            for (int i = 0; i < voteOptions.length; i++) {
                voteOptionNames[i] = voteOptions[i].getPath().replace("_", " ");
            }
        }
    }

    @Override
    protected void onEnd() {
        votes.clear();
    }

    @Override
    protected void onTick() {
        phaseTimer++;
        switch (phase) {
            case WAITING -> {
                int currentPlayers = players.size();
                int minPlayers = data.minPlayers();
                if (currentPlayers >= minPlayers || (phaseTimer > LOBBY_WAIT_TICKS && currentPlayers >= 1)) {
                    startVoting();
                } else if (phaseTimer % 20 == 0) {
                    broadcastTitle(
                            Component.translatable("legacy.minigame.lobby.waiting"),
                            Component.translatable("legacy.minigame.lobby.players_needed", minPlayers - currentPlayers)
                    );
                }
            }
            case VOTING -> {
                if (phaseTimer >= VOTE_TICKS) {
                    startCountdownPhase();
                } else if (phaseTimer % 20 == 0) {
                    broadcastVoteUpdate();
                    broadcastToPlayers(new S2CCountdownPayload(VOTE_TICKS - phaseTimer, false));
                }
            }
            case COUNTDOWN -> {
                int remainingTicks = COUNTDOWN_TICKS - phaseTimer;
                if (remainingTicks <= 0) {
                    dispatchMinigame();
                } else {
                    broadcastToPlayers(new S2CCountdownPayload(remainingTicks, true));
                    if (remainingTicks % 20 == 0) {
                        int seconds = remainingTicks / 20;
                        broadcastTitle(Component.literal(String.valueOf(seconds)),
                                Component.translatable("legacy.minigame.lobby.starting"));
                    }
                }
            }
        }
    }

    private void startVoting() {
        phase = LobbyPhase.VOTING;
        phaseTimer = 0;
        votes.clear();
        if (voteOptions.length > 0) {
            broadcastToPlayers(new S2CMapVoteOptionsPayload(voteOptions, voteOptionNames));
        }
    }

    private void startCountdownPhase() {
        phase = LobbyPhase.COUNTDOWN;
        phaseTimer = 0;
    }

    private void broadcastVoteUpdate() {
        if (voteOptions.length == 0) return;
        int[] counts = new int[voteOptions.length];
        votes.values().forEach(voted -> {
            for (int i = 0; i < voteOptions.length; i++) {
                if (voteOptions[i].equals(voted)) { counts[i]++; break; }
            }
        });
        broadcastToPlayers(new S2CVoteUpdatePayload(voteOptions, counts));
    }

    private void dispatchMinigame() {
        phase = LobbyPhase.DISPATCHING;
        ResourceLocation chosenMap = chooseWinningMap();
        Legacy4J.LOGGER.info("[Legacy4J Minigames] Dispatching with map {}", chosenMap);
    }

    private ResourceLocation chooseWinningMap() {
        if (voteOptions.length == 0) return data.mapId();
        Map<ResourceLocation, Integer> tally = new HashMap<>();
        votes.values().forEach(v -> tally.merge(v, 1, Integer::sum));
        return tally.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(voteOptions[0]);
    }

    public void recordVote(ServerPlayer player, ResourceLocation mapId) {
        if (phase != LobbyPhase.VOTING) return;
        for (ResourceLocation option : voteOptions) {
            if (option.equals(mapId)) {
                votes.put(player.getUUID(), mapId);
                broadcastVoteUpdate();
                return;
            }
        }
    }

    @Override
    public Minigame<LobbyMinigameController> getMinigame() {
        return Minigame.LOBBY;
    }

    public LobbyPhase getPhase() { return phase; }
    public Map<UUID, ResourceLocation> getVotes() { return Collections.unmodifiableMap(votes); }
}
