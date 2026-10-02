package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IRecipeBookPage {
    default void recipesUpdated() {
        throw new UnsupportedOperationException("Missing mixin override");
    }

    default OverlayRecipeComponent getOverlay() {
        throw new UnsupportedOperationException("Missing mixin override");
    }
}
