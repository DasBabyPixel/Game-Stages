package de.dasbabypixel.gamestages.common.data.manager.immutable;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.attribute.AbstractAttributeHolder;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * A game stage manager that is considered immutable for any restrictions/content modifications.
 * Attributes are still mutable for extensibility purposes
 *
 * @param <H> the type of game stage manager
 */
@NullMarked
public abstract class AbstractGameStageManager<H extends AbstractGameStageManager<H>> extends AbstractAttributeHolder<H> {
    private final Set<GameStage> gameStages;
    private final Set<RestrictionEntry.PreCompiled<?, ?>> restrictions;

    public AbstractGameStageManager(Collection<? extends GameStage> gameStages, Collection<? extends RestrictionEntry.PreCompiled<?, ?>> restrictions) {
        this.gameStages = Objects.requireNonNull(Set.copyOf(gameStages));
        this.restrictions = Objects.requireNonNull(Set.copyOf(restrictions));
    }

    public @Nullable GameStage get(String name) {
        var stage = new GameStage(name);
        if (!gameStages.contains(stage)) return null;
        return stage;
    }

    public Set<RestrictionEntry.PreCompiled<?, ?>> restrictions() {
        return restrictions;
    }

    public Set<GameStage> gameStages() {
        return gameStages;
    }
}
