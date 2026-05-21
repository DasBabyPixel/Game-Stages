package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei;

import mezz.jei.api.recipe.RecipeType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record RecipeAndType<T>(RecipeType<T> type, T recipe) {
}
