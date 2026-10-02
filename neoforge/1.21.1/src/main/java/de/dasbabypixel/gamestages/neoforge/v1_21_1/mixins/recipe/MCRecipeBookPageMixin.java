package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.IRecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Objects;

@NullMarked
@Mixin(RecipeBookPage.class)
@Implements(@Interface(iface = IRecipeBookPage.class, prefix = "stages$"))
public abstract class MCRecipeBookPageMixin implements IRecipeBookPage {
    @Shadow
    @Nullable
    private RecipeHolder<?> lastClickedRecipe;

    @Shadow
    @Nullable
    private RecipeCollection lastClickedRecipeCollection;

    @Shadow
    @Final
    private OverlayRecipeComponent overlay;

    public void stages$recipesUpdated() {
        overlay.recipesUpdated();
        lastClickedRecipe = null;
        lastClickedRecipeCollection = null;
    }

    public OverlayRecipeComponent stages$getOverlay() {
        return Objects.requireNonNull(overlay);
    }
}
