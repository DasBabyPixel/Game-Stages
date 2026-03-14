package de.dasbabypixel.gamestages.common.addons.recipe;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NonNull;

public interface RecipeRestrictionEntry<T extends RecipeRestrictionEntry<T, P>, P> extends RestrictionEntry<T, P> {
    @NonNull GameContent targetRecipes();

    @NonNull T setHideInJEI(boolean hideInJEI);
}
