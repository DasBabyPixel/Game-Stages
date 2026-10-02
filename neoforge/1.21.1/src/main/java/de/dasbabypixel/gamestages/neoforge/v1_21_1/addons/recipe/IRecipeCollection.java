package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Set;

public interface IRecipeCollection {
    default void updateValidRecipes() {
        throw new UnsupportedOperationException("Missing mixin override");
    }

    default boolean hasValidRecipes() {
        throw new UnsupportedOperationException("Missing mixin override");
    }

    default Set<RecipeHolder<?>> getValidRecipes() {
        throw new UnsupportedOperationException("Missing mixin override");
    }
}
