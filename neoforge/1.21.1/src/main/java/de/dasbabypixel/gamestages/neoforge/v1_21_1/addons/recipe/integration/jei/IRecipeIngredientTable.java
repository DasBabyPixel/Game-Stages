package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei;

import mezz.jei.api.recipe.RecipeType;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@NullMarked
public interface IRecipeIngredientTable {
    <V> void remove(V recipe, RecipeType<V> recipeType, Collection<Object> ingredientUids);
}
