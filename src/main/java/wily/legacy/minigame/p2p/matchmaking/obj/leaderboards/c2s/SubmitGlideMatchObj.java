package wily.legacy.minigame.p2p.matchmaking.obj.leaderboards.c2s;

import net.minecraft.resources.Identifier;

import java.time.Duration;
import java.util.Optional;

public record SubmitGlideMatchObj(Identifier map, Duration duration, Optional<Integer> score, boolean legacy4j) {
}
