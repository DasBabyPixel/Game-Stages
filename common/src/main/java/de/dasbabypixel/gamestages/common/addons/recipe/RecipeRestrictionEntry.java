package de.dasbabypixel.gamestages.common.addons.recipe;

import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface RecipeRestrictionEntry<T extends RecipeRestrictionEntry<T, P, C>, P extends RestrictionEntry.PreCompiled<P, C>, C extends CompiledRestrictionEntry<C, P>> extends RestrictionEntry<T, P, C> {
    TypedGameContent targetRecipes();

    @Override
    default TypedGameContent gameContent() {
        return targetRecipes();
    }

    T setHideInJEI(boolean hideInJEI);
}
