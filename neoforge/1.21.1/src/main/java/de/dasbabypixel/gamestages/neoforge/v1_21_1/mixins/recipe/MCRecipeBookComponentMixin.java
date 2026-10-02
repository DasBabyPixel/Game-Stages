package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.sugar.Local;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.IRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@NullMarked
@Mixin(RecipeBookComponent.class)
@Implements(@Interface(iface = IRecipeBookComponent.class, prefix = "stages$"))
public abstract class MCRecipeBookComponentMixin implements IRecipeBookComponent {
    @Shadow
    @Final
    protected GhostRecipe ghostRecipe;
    @Shadow
    @Final
    private RecipeBookPage recipeBookPage;

    @Shadow
    protected abstract void updateTabs();

    @Redirect(method = "recipesUpdated", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;updateTabs()V"))
    private void recipesUpdatedSkipUpdateTabs(RecipeBookComponent instance) {
    }

    @Inject(method = "recipesUpdated", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;updateCollections(Z)V", shift = At.Shift.AFTER))
    private void recipesUpdatedMoreInvalidations(CallbackInfo ci) {

    }

    @Inject(method = "recipesUpdated", at = @At("TAIL"))
    private void recipesUpdatedLateUpdateTabs(CallbackInfo ci) {
        updateTabs();
        recipeBookPage.recipesUpdated();
        ghostRecipe.recipesUpdated();
    }

    @Inject(method = "updateCollections", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;getValue()Ljava/lang/String;"))
    private void updateCollections(boolean resetPageNumber, CallbackInfo ci, @Local(name = "list1") List<RecipeCollection> list1) {
        list1.removeIf(c -> !c.hasValidRecipes());
    }

    public GhostRecipe stages$getGhostRecipe() {
        return Objects.requireNonNull(ghostRecipe);
    }

    public RecipeBookPage stages$getRecipeBookPage() {
        return Objects.requireNonNull(recipeBookPage);
    }
}
