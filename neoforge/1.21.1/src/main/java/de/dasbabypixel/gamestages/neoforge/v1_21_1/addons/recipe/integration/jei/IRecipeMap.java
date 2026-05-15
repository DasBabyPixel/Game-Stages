package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei;

import mezz.jei.api.recipe.RecipeType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IRecipeMap {
    <T> void removeRecipe(RecipeType<T> recipeType, T recipe);
}
