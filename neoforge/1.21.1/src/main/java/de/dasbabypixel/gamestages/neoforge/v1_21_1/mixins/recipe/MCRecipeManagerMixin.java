package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeThreadLocal;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;
import java.util.stream.Stream;

@NullMarked
@Mixin(RecipeManager.class)
public class MCRecipeManagerMixin {
    @ModifyReceiver(method = "getRecipesFor", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"))
    private <H extends RecipeHolder<T>, T extends Recipe<I>, I extends RecipeInput> Stream<H> getRecipesFor(Stream<H> instance, Predicate<? super T> predicate) {
        var recipes = RecipeThreadLocal.get();
        var stages = recipes.stagesOrRecord();
        return instance.filter(recipeHolder -> {
            var entry = VRecipeAddon.getEntry(stages, recipeHolder);
            if (entry == null) return true;
            return entry.predicate().test();
        });
    }

    @ModifyReceiver(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;findFirst()Ljava/util/Optional;"))
    private <T extends RecipeHolder<H>, H extends Recipe<?>> Stream<T> getRecipeFor(Stream<T> stream) {
        var recipes = RecipeThreadLocal.get();
        var stages = recipes.stagesOrRecord();
        return stream.filter(recipeHolder -> {
            var entry = VRecipeAddon.getEntry(stages, recipeHolder);
            if (entry == null) return true; // Unrestricted
            return entry.predicate().test();
        });
    }

    @WrapOperation(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Recipe;matches(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Z"))
    private <R extends Recipe<T>, T extends RecipeInput> boolean ifGetRecipeFor(Recipe<T> instance, T t, Level level, Operation<Boolean> original, @Local(argsOnly = true) RecipeHolder<R> lastRecipe) {
        var recipes = RecipeThreadLocal.get();
        var stages = recipes.stagesOrRecord();
        var entry = VRecipeAddon.getEntry(stages, lastRecipe);
        if (entry != null && !entry.predicate().test()) return false;
        return original.call(instance, t, level);
    }
}
