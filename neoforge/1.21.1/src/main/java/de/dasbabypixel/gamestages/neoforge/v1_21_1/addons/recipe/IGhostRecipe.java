package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IGhostRecipe {
    default void recipesUpdated() {
        throw new UnsupportedOperationException("Missing mixin override");
    }
}
