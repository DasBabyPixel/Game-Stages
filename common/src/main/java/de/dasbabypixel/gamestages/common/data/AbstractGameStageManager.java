package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractGameStageManager {
    protected final Set<GameStage> gameStages = new HashSet<>();
    protected final List<RestrictionEntry<?, ?>> restrictions = new ArrayList<>();
    private final Map<Attribute<?>, Object> attributeMap = new HashMap<>();

    public void add(@NonNull GameStage gameStage) {
        if (!mayMutate()) throw new IllegalStateException("Cannot mutate");
        if (containsKey(gameStage)) {
            throw new IllegalArgumentException("Multiple GameStages have the same name");
        }
        add0(gameStage);
    }

    public @Nullable GameStage get(@NonNull String name) {
        var stage = new GameStage(name);
        if (!containsKey(stage)) return null;
        return stage;
    }

    @SuppressWarnings("unchecked")
    public <T> @NonNull T get(@NonNull Attribute<? extends T> attribute) {
        return (T) Objects.requireNonNull(attributeMap.computeIfAbsent(attribute, a -> a.defaultValue().apply(this)));
    }

    public <T extends RestrictionEntry<T, ?>> @NonNull T addRestriction(@NonNull T restriction) {
        this.restrictions.add(restriction);
        return restriction;
    }

    public void set(@NonNull List<@NonNull GameStage> gameStages) {
        reset();
        gameStages.forEach(this::add);
    }

    public void reset() {
        clear0();
    }

    public @NonNull Set<@NonNull GameStage> gameStages() {
        return gameStages;
    }

    public @NonNull List<@NonNull RestrictionEntry<?, ?>> restrictions() {
        return restrictions;
    }

    protected void clear0() {
        gameStages.clear();
        restrictions.clear();
        attributeMap.clear();
    }

    protected boolean containsKey(@NonNull GameStage gameStage) {
        return gameStages.contains(gameStage);
    }

    protected void add0(@NonNull GameStage stage) {
        gameStages.add(stage);
    }

    protected boolean mayMutate() {
        return true;
    }

    public record Attribute<T>(
            @NonNull Function<@NonNull AbstractGameStageManager, ? extends @NonNull T> defaultValue) {
        public Attribute(@NonNull Supplier<? extends @NonNull T> defaultValue) {
            this(ignore -> defaultValue.get());
        }
    }
}
