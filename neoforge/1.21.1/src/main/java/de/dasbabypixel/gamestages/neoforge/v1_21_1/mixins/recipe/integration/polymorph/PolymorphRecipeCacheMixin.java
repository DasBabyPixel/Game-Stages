package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe.integration.polymorph;

import com.illusivesoulworks.polymorph.common.capability.RecipeCache;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeThreadLocal;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@NullMarked
@Mixin(value = RecipeCache.class, remap = false)
public class PolymorphRecipeCacheMixin {
    @WrapOperation(method = "compute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/List;"))
    private <T extends Recipe<I>, I extends RecipeInput> List<RecipeHolder<T>> compute(RecipeManager instance, RecipeType<T> recipeType, I input, Level level, Operation<List<RecipeHolder<T>>> original) {
        var recipes = RecipeThreadLocal.get();
        var stages = recipes.stagesOrRecord();
        if (stages == null) {
            return original.call(instance, recipeType, input, level);
        }
        // We push null to ensure all recipes are loaded from RecipeManager.
        recipes.pushStages(null);
        try {
            return original.call(instance, recipeType, input, level);
        } finally {
            recipes.popStages();
        }
    }
}
