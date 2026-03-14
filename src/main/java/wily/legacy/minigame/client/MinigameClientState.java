package wily.legacy.minigame.client;

import net.minecraft.resources.ResourceLocation;
import wily.legacy.minigame.networking.S2CLeaderboardPayload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Client-side state for active minigames.
 * Tracks current minigame, map, countdown, leaderboard, and voting state.
 */
public class MinigameClientState {

    private static String activeMinigame = null;
    private static ResourceLocation activeMap = null;
    private static int maxPlayers = 0;
    private static int countdownTicks = -1;
    private static boolean countdownStarting = false;
    private static ResourceLocation transitionMap = null;
    private static int transitionTicks = 0;

    private static ResourceLocation[] voteMapOptions = new ResourceLocation[0];
    private static String[] voteMapNames = new String[0];
    private static int[] voteMapCounts = new int[0];
    private static ResourceLocation myVote = null;

    private static S2CLeaderboardPayload.LeaderboardEntry[] leaderboard = new S2CLeaderboardPayload.LeaderboardEntry[0];
    private static int glideCheckpoint = 0;
    private static int glideTotalCheckpoints = 0;
    private static long glideCheckpointTimeMs = 0;

    public static final List<Runnable> ON_MINIGAME_START = new ArrayList<>();
    public static final List<Runnable> ON_MINIGAME_END = new ArrayList<>();
    public static final List<Runnable> ON_VOTE_UPDATE = new ArrayList<>();

    public static void onMinigameStart(String minigameName, ResourceLocation mapId, int maxP) {
        activeMinigame = minigameName;
        activeMap = mapId;
        maxPlayers = maxP;
        countdownTicks = -1;
        leaderboard = new S2CLeaderboardPayload.LeaderboardEntry[0];
        ON_MINIGAME_START.forEach(Runnable::run);
    }

    public static void onMinigameEnd(String minigameName, String winnerName) {
        activeMinigame = null;
        activeMap = null;
        countdownTicks = -1;
        myVote = null;
        ON_MINIGAME_END.forEach(Runnable::run);
    }

    public static void onMapTransition(ResourceLocation mapId, String minigameName, int durationTicks) {
        transitionMap = mapId;
        transitionTicks = durationTicks;
    }

    public static void onMapVoteOptions(ResourceLocation[] maps, String[] names) {
        voteMapOptions = maps;
        voteMapNames = names;
        voteMapCounts = new int[maps.length];
        myVote = null;
        ON_VOTE_UPDATE.forEach(Runnable::run);
    }

    public static void onVoteUpdate(ResourceLocation[] maps, int[] counts) {
        voteMapCounts = counts;
        ON_VOTE_UPDATE.forEach(Runnable::run);
    }

    public static void onCountdown(int ticks, boolean starting) {
        countdownTicks = ticks;
        countdownStarting = starting;
    }

    public static void updateLeaderboard(S2CLeaderboardPayload.LeaderboardEntry[] entries) {
        leaderboard = entries;
    }

    public static void onGlideCheckpoint(int index, int total, long timeMs) {
        glideCheckpoint = index;
        glideTotalCheckpoints = total;
        glideCheckpointTimeMs = timeMs;
    }

    public static void tick() {
        if (transitionTicks > 0) transitionTicks--;
    }

    public static void setMyVote(ResourceLocation mapId) {
        myVote = mapId;
    }

    public static boolean isInMinigame() { return activeMinigame != null; }
    public static String getActiveMinigame() { return activeMinigame; }
    public static ResourceLocation getActiveMap() { return activeMap; }
    public static int getMaxPlayers() { return maxPlayers; }
    public static int getCountdownTicks() { return countdownTicks; }
    public static boolean isCountdownStarting() { return countdownStarting; }
    public static boolean isInTransition() { return transitionTicks > 0; }
    public static ResourceLocation getTransitionMap() { return transitionMap; }
    public static ResourceLocation[] getVoteMapOptions() { return voteMapOptions; }
    public static String[] getVoteMapNames() { return voteMapNames; }
    public static int[] getVoteMapCounts() { return voteMapCounts; }
    public static ResourceLocation getMyVote() { return myVote; }
    public static S2CLeaderboardPayload.LeaderboardEntry[] getLeaderboard() { return leaderboard; }
    public static int getGlideCheckpoint() { return glideCheckpoint; }
    public static int getGlideTotalCheckpoints() { return glideTotalCheckpoints; }
    public static long getGlideCheckpointTimeMs() { return glideCheckpointTimeMs; }
}
