package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@NullMarked
@Mixin(RecipeBookTabButton.class)
public class RecipeBookTabButtonMixin {
    @ModifyExpressionValue(method = "updateVisibility", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeCollection;hasKnownRecipes()Z"))
    private boolean updateVisibility(boolean original, @Local RecipeCollection recipecollection) {
        if (!original) return false;
        return recipecollection.hasValidRecipes();
    }
}
