package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.restriction.types.RestrictionEntry;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class AbstractGameStageManager {
    protected final Set<GameStage> gameStages = new HashSet<>();
    protected final List<RestrictionEntry<?, ?>> restrictions = new ArrayList<>();
    private final Map<Attribute<?>, Object> attributeMap = new HashMap<>();

    public void add(GameStage gameStage) {
        if (!mayMutate()) throw new IllegalStateException("Cannot mutate");
        if (containsKey(gameStage)) {
            throw new IllegalArgumentException("Multiple GameStages have the same name");
        }
        add0(gameStage);
    }

    public @Nullable GameStage get(String name) {
        var stage = new GameStage(name);
        if (!containsKey(stage)) return null;
        return stage;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Attribute<? extends T> attribute) {
        return (T) attributeMap.computeIfAbsent(attribute, a -> a.defaultValue().apply(this));
    }

    public <T extends RestrictionEntry<T, ?>> T addRestriction(@NonNull T restriction) {
        this.restrictions.add(restriction);
        return restriction;
    }

    public void set(List<GameStage> gameStages) {
        reset();
        gameStages.forEach(this::add);
    }

    public void reset() {
        clear0();
    }

    public Set<GameStage> gameStages() {
        return gameStages;
    }

    public List<RestrictionEntry<?, ?>> restrictions() {
        return restrictions;
    }

    protected void clear0() {
        gameStages.clear();
        restrictions.clear();
        attributeMap.clear();
    }

    protected boolean containsKey(GameStage gameStage) {
        return gameStages.contains(gameStage);
    }

    protected void add0(GameStage stage) {
        gameStages.add(stage);
    }

    protected boolean mayMutate() {
        return true;
    }

    public record Attribute<T>(Function<@NonNull AbstractGameStageManager, ? extends @NonNull T> defaultValue) {
        public Attribute(Supplier<? extends @NonNull T> defaultValue) {
            this(ignore -> defaultValue.get());
        }
    }
}
