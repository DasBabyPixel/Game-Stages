package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.IRecipeCollection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@NullMarked
@Mixin(RecipeCollection.class)
@Implements(@Interface(iface = IRecipeCollection.class, prefix = "stages$"))
public abstract class MCRecipeCollectionMixin implements IRecipeCollection {
    @Unique
    private Set<RecipeHolder<?>> stages$valid = Set.of();
    @Shadow
    @Final
    private Set<RecipeHolder<?>> known;

    @Inject(method = "updateKnownRecipes", at = @At("HEAD"))
    private void updateKnownRecipesClearOld(RecipeBook book, CallbackInfo ci) {
        // Fixes a bug where the client doesn't clear the Set of known recipes
        known.clear();
    }

    @Inject(method = "updateKnownRecipes", at = @At("TAIL"))
    private void updateKnownRecipes(RecipeBook book, CallbackInfo ci) {
        // Valid recipes are a subset of known recipes. Need to update valid
        stages$updateValidRecipes();
    }

    @ModifyExpressionValue(method = "getDisplayRecipes", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    private boolean getDisplayRecipes(boolean original, @Local(name = "recipeholder") RecipeHolder<?> recipeholder) {
        if (!original) return false;
        var stages = ClientGameStageManager.stages();
        var entry = VRecipeAddon.getEntry(stages, recipeholder);
        return entry == null || entry.predicate().test();
    }

    public Set<RecipeHolder<?>> stages$getValidRecipes() {
        return stages$valid;
    }

    public boolean stages$hasValidRecipes() {
        return !stages$valid.isEmpty();
    }

    public void stages$updateValidRecipes() {
        if (known.isEmpty()) {
            stages$valid = Set.of();
        } else {
            var valid = new HashSet<RecipeHolder<?>>();
            for (var recipeHolder : this.known) {
                var player = Minecraft.getInstance().player;
                var entry = VRecipeAddon.getEntry(player == null ? null : player.getGameStages(), recipeHolder);
                if (entry == null || entry.predicate().test()) {
                    valid.add(recipeHolder);
                }
            }
            stages$valid = Set.copyOf(valid);
        }
    }
}
