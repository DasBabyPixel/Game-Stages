package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeThreadLocal;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeCache;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@NullMarked
@Mixin(RecipeCache.class)
public class MCRecipeCacheMixin {
    @WrapOperation(method = "get", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeCache$Entry;matches(Lnet/minecraft/world/item/crafting/CraftingInput;)Z"))
    private boolean matches(RecipeCache.Entry instance, CraftingInput input, Operation<Boolean> original) {
        var matches = original.call(instance, input);
        if (!matches) return false;
        var holder = instance.value();
        if (holder == null) {
            // We stored a null-entry in the cache. This means that no recipe exists for the given input.
            // Because a recipe might be restricted, we need to re-check if a recipe exists now, so we'll
            // just invalidate the cache entry by matching to false.
            return false;
        }
        var recipes = RecipeThreadLocal.get();
        var stages = recipes.stagesOrRecord();
        var entry = VRecipeAddon.getEntry(stages, holder);
        if (entry == null) return true;
        return entry.predicate().test();
    }
}
