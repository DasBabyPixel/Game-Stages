package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe.integration.jei;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei.IIngredientToRecipesMap;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei.IRecipeIngredientTable;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.library.recipes.collect.IngredientToRecipesMap;
import mezz.jei.library.recipes.collect.RecipeIngredientTable;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@NullMarked
@Mixin(value = RecipeIngredientTable.class, remap = false)
@Implements(@Interface(iface = IRecipeIngredientTable.class, prefix = "stages_recipes$"))
public class JEIRecipeIngredientTableMixin {
    @Shadow
    @Final
    private Map<RecipeType<?>, IngredientToRecipesMap<?>> map;

    @SuppressWarnings("unchecked")
    public <V> void stages_recipes$remove(V recipe, RecipeType<V> recipeType, Collection<Object> ingredientUids) {
        var m = Objects.requireNonNull(map.get(recipeType));
        var it = (IIngredientToRecipesMap<V>) m;
        it.remove(recipe, ingredientUids);
    }
}
