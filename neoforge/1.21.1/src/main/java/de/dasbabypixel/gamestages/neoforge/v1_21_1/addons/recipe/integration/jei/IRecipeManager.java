package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei;

import mezz.jei.api.recipe.RecipeType;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface IRecipeManager {
    <T> void removeRecipes(RecipeType<T> recipeType, List<T> recipes);
}
