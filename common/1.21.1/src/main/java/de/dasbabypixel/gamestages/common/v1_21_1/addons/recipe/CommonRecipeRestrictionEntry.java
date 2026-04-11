package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.addons.recipe.AbstractRecipeRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class CommonRecipeRestrictionEntry<T extends CommonRecipeRestrictionEntry<T, P>, P> extends AbstractRecipeRestrictionEntry<T, P> {
    public CommonRecipeRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, GameContent targetRecipes) {
        super(predicate, origin, targetRecipes);
    }
}
