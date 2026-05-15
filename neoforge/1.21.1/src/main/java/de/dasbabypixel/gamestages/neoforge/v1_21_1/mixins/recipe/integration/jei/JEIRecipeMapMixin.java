package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe.integration.jei;

import com.llamalad7.mixinextras.sugar.Local;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei.IRecipeIngredientTable;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei.IRecipeMap;
import mezz.jei.api.ingredients.IIngredientSupplier;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.library.recipes.collect.RecipeIngredientTable;
import mezz.jei.library.recipes.collect.RecipeMap;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@NullMarked
@Mixin(value = RecipeMap.class, remap = false)
@Implements(@Interface(iface = IRecipeMap.class, prefix = "stages_recipe$"))
public class JEIRecipeMapMixin {
    @Unique
    private final Map<RecipeType<?>, Map<Object, Set<Object>>> stages_recipe$ingredientUidsByRecipe = new HashMap<>();
    @Shadow
    @Final
    private RecipeIngredientTable recipeTable;

    @Inject(method = "addRecipe", at = @At(value = "INVOKE", target = "Ljava/util/Set;isEmpty()Z"))
    private <T> void addRecipe(RecipeType<T> recipeType, T recipe, IIngredientSupplier ingredientSupplier, CallbackInfo ci, @Local(name = "ingredientUids") Set<Object> ingredientUids) {
        var map = stages_recipe$ingredientUidsByRecipe.computeIfAbsent(recipeType, i -> new HashMap<>());
        map.put(recipe, ingredientUids);
    }

    public <T> void stages_recipe$removeRecipe(RecipeType<T> recipeType, T recipe) {
        var map = Objects.requireNonNull(stages_recipe$ingredientUidsByRecipe.get(recipeType));
        var ingredientUids = Objects.requireNonNull(map.remove(recipe));
        var it = (IRecipeIngredientTable) recipeTable;
        it.remove(recipe, recipeType, ingredientUids);
    }
}
