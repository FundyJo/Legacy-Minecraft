package wily.legacy.fantasy.util;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;
public final class GameRuleStore {
    private final Reference2ObjectMap<GameRules.Key<?>, Object> rules = new Reference2ObjectOpenHashMap<>();
    public <T extends GameRules.Value<T>> void set(GameRules.Key<T> key, T value) {
        this.rules.put(key, value);
    }
    @SuppressWarnings("unchecked")
    public <T extends GameRules.Value<T>> T get(GameRules.Key<T> key) {
        return (T) this.rules.get(key);
    }
    public boolean contains(GameRules.Key<?> key) {
        return this.rules.containsKey(key);
    }
    public void applyTo(GameRules rules, @Nullable MinecraftServer server) {
        Reference2ObjectMaps.fastForEach(this.rules, entry -> {
            @SuppressWarnings("rawtypes")
            GameRules.Key key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Boolean) {
                @SuppressWarnings("unchecked")
                GameRules.BooleanValue rule = rules.getRule((GameRules.Key<GameRules.BooleanValue>) key);
                rule.set((Boolean) value, server);
            } else if (value instanceof Integer) {
                @SuppressWarnings("unchecked")
                GameRules.IntegerValue rule = rules.getRule((GameRules.Key<GameRules.IntegerValue>) key);
                rule.set((Integer) value, server);
            }
        });
    }
}
