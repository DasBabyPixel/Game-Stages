package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei;

import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@NullMarked
public interface IRecipeTypeData<T> {
    void removeRecipes(Collection<T> recipes);
}
