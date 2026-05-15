package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe.integration.jei;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei.IIngredientToRecipesMap;
import mezz.jei.library.recipes.collect.IngredientToRecipesMap;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@NullMarked
@Mixin(value = IngredientToRecipesMap.class, remap = false)
@Implements(@Interface(iface = IIngredientToRecipesMap.class, prefix = "stages_recipes$"))
public class JEIIngredientToRecipesMapMixin<T> {
    @Shadow
    @Final
    private Map<Object, ArrayList<T>> uidToRecipes;

    public void stages_recipes$remove(T recipe, Collection<Object> ingredientUids) {
        for (var ingredientUid : ingredientUids) {
            var list = Objects.requireNonNull(uidToRecipes.get(ingredientUid));
            list.remove(recipe);
            if (list.isEmpty()) uidToRecipes.remove(ingredientUid);
        }
    }
}
