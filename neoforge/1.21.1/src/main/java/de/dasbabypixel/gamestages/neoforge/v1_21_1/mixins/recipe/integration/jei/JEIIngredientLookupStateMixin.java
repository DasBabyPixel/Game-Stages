package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe.integration.jei;

import com.google.common.base.Preconditions;
import com.llamalad7.mixinextras.sugar.Local;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.recipes.lookups.IFocusedRecipes;
import mezz.jei.gui.recipes.lookups.IngredientLookupState;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@NullMarked
@Mixin(value = IngredientLookupState.class, remap = false)
public class JEIIngredientLookupStateMixin {
    @Shadow
    private int recipeCategoryIndex;
    @Shadow
    private int recipeIndex;
    @Shadow
    private @Nullable IFocusedRecipes<?> focusedRecipes;

    @Inject(method = "moveToRecipeCategory", at = @At(value = "INVOKE", target = "Lmezz/jei/gui/recipes/lookups/IngredientLookupState;moveToRecipeCategoryIndex(I)V"), cancellable = true)
    private void move(IRecipeCategory<?> recipeCategory, CallbackInfoReturnable<Boolean> cir, @Local(name = "recipeCategoryIndex") int recipeCategoryIndex) {
        Preconditions.checkArgument(recipeCategoryIndex >= 0, "Recipe category index cannot be negative.");
        var oldCat = this.recipeCategoryIndex;
        this.recipeCategoryIndex = recipeCategoryIndex;
        this.recipeIndex = 0;
        if (oldCat != recipeCategoryIndex) focusedRecipes = null;
        cir.setReturnValue(true);
        cir.cancel();
    }
}
