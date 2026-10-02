package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe.integration.jei;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve.SieveCategory;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.gui.overlay.elements.RecipeBookmarkElement;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@NullMarked
@Mixin(RecipeBookmarkElement.class)
public class JEIRecipeBookmarkElementMixin {
    @Inject(method = "getTooltip(Lmezz/jei/common/gui/JeiTooltip;Lmezz/jei/api/ingredients/IIngredientRenderer;Lmezz/jei/api/ingredients/IIngredientHelper;Z)V", at = @At(value = "INVOKE", target = "Lmezz/jei/api/recipe/category/IRecipeCategory;getRegistryName(Ljava/lang/Object;)Lnet/minecraft/resources/ResourceLocation;"))
    private <I> void getTooltipBefore(JeiTooltip tooltip, IIngredientRenderer<I> ingredientRenderer, IIngredientHelper<I> ingredientHelper, boolean pinned, CallbackInfo ci) {
        SieveCategory.IGNORE_TOOLTIP.set(true);
    }

    @Inject(method = "getTooltip(Lmezz/jei/common/gui/JeiTooltip;Lmezz/jei/api/ingredients/IIngredientRenderer;Lmezz/jei/api/ingredients/IIngredientHelper;Z)V", at = @At(value = "INVOKE", target = "Lmezz/jei/api/recipe/category/IRecipeCategory;getRegistryName(Ljava/lang/Object;)Lnet/minecraft/resources/ResourceLocation;", shift = At.Shift.AFTER))
    private <I> void getTooltipAfter(JeiTooltip tooltip, IIngredientRenderer<I> ingredientRenderer, IIngredientHelper<I> ingredientHelper, boolean pinned, CallbackInfo ci) {
        SieveCategory.IGNORE_TOOLTIP.set(false);
    }
}
