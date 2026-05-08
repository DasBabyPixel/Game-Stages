package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeThreadLocal;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeCache;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@NullMarked
@Mixin(RecipeCache.class)
public class MCRecipeCacheMixin {
    @Inject(method = "insert", at = @At(value = "INVOKE", target = "Ljava/lang/System;arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V"), cancellable = true)
    private void insert(CraftingInput input, @Nullable RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci) {
        if (recipe == null) {
            // We don't want to insert null entries to the cache. They could be null because they were filtered out based on game stage predicates.
            ci.cancel();
        }
    }

    @Redirect(method = "get", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeCache$Entry;matches(Lnet/minecraft/world/item/crafting/CraftingInput;)Z"))
    private boolean matches(RecipeCache.Entry instance, CraftingInput input) {
        var matches = instance.matches(input);
        if (!matches) return false;
        var holder = instance.value();
        if (holder == null)
            throw new IllegalStateException("Must not happen. Please report this as a bug to Pixel's GameStages");
        var recipes = RecipeThreadLocal.get();
        var stages = recipes.stagesOrRecord();
        var entry = VRecipeAddon.getEntry(stages, holder);
        if (entry == null) return true;
        return entry.predicate().test();
    }
}
