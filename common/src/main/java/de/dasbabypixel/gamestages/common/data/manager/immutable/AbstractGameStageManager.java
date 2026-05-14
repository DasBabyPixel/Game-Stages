package de.dasbabypixel.gamestages.common.data.manager.immutable;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.attribute.AttributeEntry;
import de.dasbabypixel.gamestages.common.data.attribute.ImmutableAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleImmutableAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleImmutableAttributeHolder;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * A game stage manager that is considered immutable for any restrictions/content modifications.
 * Attributes are still mutable for extensibility purposes
 *
 * @param <Self> the type of game stage manager
 */
@NullMarked
public abstract class AbstractGameStageManager<Self extends AbstractGameStageManager<Self>> extends SimpleImmutableAttributeHolder<Self> {
    public static final ImmutableAttribute<AbstractGameStageManager<?>, Integer> VERSION = new SimpleImmutableAttribute<>();
    public static final ImmutableAttribute<AbstractGameStageManager<?>, Set<GameStage>> GAME_STAGES = new SimpleImmutableAttribute<>();
    public static final ImmutableAttribute<AbstractGameStageManager<?>, Set<RestrictionEntry.PreCompiled<?, ?>>> RESTRICTIONS = new SimpleImmutableAttribute<>();

    public AbstractGameStageManager(Collection<AttributeEntry<? super Self, ?>> attributes) {
        super(attributes);
    }

    public @Nullable GameStage get(String name) {
        var stage = new GameStage(name);
        if (!gameStages().contains(stage)) return null;
        return stage;
    }

    public Set<RestrictionEntry.PreCompiled<?, ?>> restrictions() {
        return get(RESTRICTIONS);
    }

    public Set<GameStage> gameStages() {
        return get(GAME_STAGES);
    }
}
