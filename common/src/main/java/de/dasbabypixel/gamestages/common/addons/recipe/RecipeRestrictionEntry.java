package de.dasbabypixel.gamestages.common.addons.recipe;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface RecipeRestrictionEntry<T extends RecipeRestrictionEntry<T, P>, P> extends RestrictionEntry<T, P> {
    GameContent targetRecipes();

    T setHideInJEI(boolean hideInJEI);
}
