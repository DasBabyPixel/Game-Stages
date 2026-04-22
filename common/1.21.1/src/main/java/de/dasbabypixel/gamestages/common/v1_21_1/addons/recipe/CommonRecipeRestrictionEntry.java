package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.addons.recipe.AbstractRecipeRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.recipe.RecipeCollection;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class CommonRecipeRestrictionEntry<T extends CommonRecipeRestrictionEntry<T, P, C>, P extends RestrictionEntry.PreCompiled<P, C>, C extends CompiledRestrictionEntry<C, P>> extends AbstractRecipeRestrictionEntry<T, P, C> {
    public CommonRecipeRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, RecipeCollection targetRecipes) {
        super(predicate, origin, targetRecipes);
    }

    public abstract CommonRecipeRestrictionEntry<T, P, C> copyWith(PreparedRestrictionPredicate predicate, RecipeCollection targetRecipes);
}
