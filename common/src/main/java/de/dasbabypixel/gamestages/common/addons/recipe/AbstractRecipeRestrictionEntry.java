package de.dasbabypixel.gamestages.common.addons.recipe;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.AbstractRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractRecipeRestrictionEntry<T extends AbstractRecipeRestrictionEntry<T, P>, P> extends AbstractRestrictionEntry<T, P> implements RecipeRestrictionEntry<T, P> {
    private final GameContent targetRecipes;
    private final PreparedRestrictionPredicate predicate;
    private boolean hideInJEI = true;

    public AbstractRecipeRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, GameContent targetRecipes) {
        super(origin);
        this.predicate = predicate;
        this.targetRecipes = targetRecipes;
    }

    public PreparedRestrictionPredicate predicate() {
        return predicate;
    }

    @Override
    public GameContent targetRecipes() {
        return targetRecipes;
    }

    @Override
    public T setHideInJEI(boolean hideInJEI) {
        this.hideInJEI = hideInJEI;
        return self();
    }

    public boolean hideInJEI() {
        return hideInJEI;
    }
}
