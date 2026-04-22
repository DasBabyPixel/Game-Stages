package de.dasbabypixel.gamestages.common.addons.recipe;

import de.dasbabypixel.gamestages.common.data.restriction.AbstractRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractRecipeRestrictionEntry<T extends AbstractRecipeRestrictionEntry<T, P, C>, P extends RestrictionEntry.PreCompiled<P, C>, C extends CompiledRestrictionEntry<C, P>> extends AbstractRestrictionEntry<T, P, C> implements RecipeRestrictionEntry<T, P, C> {
    private final RecipeCollection targetRecipes;
    private final PreparedRestrictionPredicate predicate;
    private boolean hideInJEI = true;

    public AbstractRecipeRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, RecipeCollection targetRecipes) {
        super(origin);
        this.predicate = predicate;
        this.targetRecipes = targetRecipes;
    }

    public PreparedRestrictionPredicate predicate() {
        return predicate;
    }

    @Override
    public RecipeCollection targetRecipes() {
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
