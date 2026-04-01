package de.dasbabypixel.gamestages.common.addons.recipe;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.AbstractRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import org.jspecify.annotations.NonNull;

public abstract class AbstractRecipeRestrictionEntry<T extends AbstractRecipeRestrictionEntry<T, P>, P> extends AbstractRestrictionEntry<T, P> implements RecipeRestrictionEntry<T, P> {
    private final @NonNull GameContent targetRecipes;
    private final @NonNull PreparedRestrictionPredicate predicate;
    private boolean hideInJEI = true;

    public AbstractRecipeRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate, @NonNull RestrictionEntryOrigin origin, @NonNull GameContent targetRecipes) {
        super(origin);
        this.predicate = predicate;
        this.targetRecipes = targetRecipes;
    }

    public @NonNull PreparedRestrictionPredicate predicate() {
        return predicate;
    }

    @Override
    public @NonNull GameContent targetRecipes() {
        return targetRecipes;
    }

    @Override
    public @NonNull T setHideInJEI(boolean hideInJEI) {
        this.hideInJEI = hideInJEI;
        return self();
    }

    public boolean hideInJEI() {
        return hideInJEI;
    }
}
