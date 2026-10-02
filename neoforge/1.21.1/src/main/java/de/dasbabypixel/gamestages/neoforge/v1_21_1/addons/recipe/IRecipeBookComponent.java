package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IRecipeBookComponent {
    default GhostRecipe getGhostRecipe() {
        throw new UnsupportedOperationException("Missing mixin override");
    }

    default RecipeBookPage getRecipeBookPage() {
        throw new UnsupportedOperationException("Missing mixin override");
    }
}
