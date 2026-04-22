package de.dasbabypixel.gamestages.common.data.manager.mutable;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.attribute.AbstractAttributeHolder;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A game stage manager that is meant to be configured. A new instance should be created when the configuration changes, since a lot of recomputation will need to be done.
 *
 * @param <H>
 */
@NullMarked
public abstract class AbstractMutableGameStageManager<H extends AbstractMutableGameStageManager<H>> extends AbstractAttributeHolder<H> {
    protected final Set<GameStage> gameStages = new HashSet<>();
    protected final List<RestrictionEntry<?, ?, ?>> restrictions = new ArrayList<>();

    public void add(GameStage gameStage) {
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

    public <T extends RestrictionEntry<T, ?, ?>> T addRestriction(T restriction) {
        this.restrictions.add(restriction);
        return restriction;
    }

    public void addAll(Collection<? extends GameStage> gameStages) {
        gameStages.forEach(this::add);
    }

    public Set<GameStage> gameStages() {
        return gameStages;
    }

    public List<RestrictionEntry<?, ?, ?>> restrictions() {
        return restrictions;
    }

    protected boolean containsKey(GameStage gameStage) {
        return gameStages.contains(gameStage);
    }

    protected void add0(GameStage stage) {
        gameStages.add(stage);
    }
}
