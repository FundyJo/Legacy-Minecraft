package wily.legacy.minigame;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Registry of known Minimega minigame types.
 *
 * <p><b>Source parity note:</b> {@code Minimega-Project/minimega-decomp} contains only
 * {@code NONE} (id=0), {@code GLIDE} (id=3), {@code FISTFIGHT} (id=70), and
 * {@code LOBBY} (id=99). {@code BATTLE} (id=1) and {@code TUMBLE} (id=2) are not present
 * in the available decompiled source; they are Legacy4J additions that anticipate future
 * Minimega support. Their integer IDs and serialized names are educated best-effort values
 * and must be re-verified when original Battle/Tumble source becomes available.
 */
public final class Minigame implements Iterable<Minigame> {
    public static final Minigame NONE = new Minigame(0, "none", 0.0F, false);
    /** Legacy4J addition — not present in Minimega-Project/minimega-decomp. ID/name unverified. */
    public static final Minigame BATTLE = new Minigame(1, "battle", 0.67F, true);
    /** Legacy4J addition — not present in Minimega-Project/minimega-decomp. ID/name unverified. */
    public static final Minigame TUMBLE = new Minigame(2, "tumble", 0.32F, false);
    public static final Minigame GLIDE = new Minigame(3, "glide", 0.93F, true);
    public static final Minigame FISTFIGHT = new Minigame(70, "fistfight", 1.0F, true);
    public static final Minigame LOBBY = new Minigame(99, "lobby", 0.75F, true);

    private static final List<Minigame> VALUES = List.of(NONE, BATTLE, TUMBLE, GLIDE, FISTFIGHT, LOBBY);
    private static final Map<Integer, Minigame> BY_ID = new LinkedHashMap<>();
    private static final Map<String, Minigame> BY_NAME = new LinkedHashMap<>();

    public static final Codec<Minigame> CODEC = Codec.either(Codec.INT, Codec.STRING).xmap(
            either -> either.map(Minigame::fromId, Minigame::fromId),
            minigame -> com.mojang.datafixers.util.Either.left(minigame.id)
    );

    public static final StreamCodec<ByteBuf, Minigame> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public Minigame decode(ByteBuf buffer) {
            return Minigame.fromId(ByteBufCodecs.VAR_INT.decode(buffer));
        }

        @Override
        public void encode(ByteBuf buffer, Minigame value) {
            ByteBufCodecs.VAR_INT.encode(buffer, value.id);
        }
    };

    static {
        for (Minigame minigame : VALUES) {
            BY_ID.put(minigame.id, minigame);
            BY_NAME.put(minigame.name, minigame);
        }
    }

    private final int id;
    private final String name;
    private final float progress;
    private final boolean playable;

    private Minigame(int id, String name, float progress, boolean playable) {
        this.id = id;
        this.name = name;
        this.progress = progress;
        this.playable = playable;
    }

    public static Minigame fromId(int id) {
        Minigame minigame = BY_ID.get(id);
        if (minigame == null) {
            throw new IllegalArgumentException("Unknown minigame id: " + id);
        }
        return minigame;
    }

    public static Minigame fromId(String id) {
        Minigame minigame = BY_NAME.get(id.toLowerCase(Locale.ROOT));
        if (minigame == null) {
            throw new IllegalArgumentException("Unknown minigame name: " + id);
        }
        return minigame;
    }

    public static List<Minigame> values() {
        return VALUES;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getProgress() {
        return progress;
    }

    public boolean isPlayable() {
        return playable;
    }

    public String tId() {
        return name;
    }

    public boolean isActualMinigame() {
        return this != NONE && this != LOBBY;
    }

    @Override
    public Iterator<Minigame> iterator() {
        return VALUES.iterator();
    }

    @Override
    public String toString() {
        return "Minigame[" + name + "]";
    }
}
