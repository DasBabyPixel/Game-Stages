package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe.integration.jei;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei.IRecipeManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei.IRecipeManagerInternal;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.library.recipes.RecipeManager;
import mezz.jei.library.recipes.RecipeManagerInternal;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@NullMarked
@Mixin(value = RecipeManager.class, remap = false)
@Implements(@Interface(iface = IRecipeManager.class, prefix = "stages_recipe$"))
public class JEIRecipeManagerMixin {
    @Shadow
    @Final
    private RecipeManagerInternal internal;

    public <T> void stages_recipe$removeRecipes(RecipeType<T> recipeType, List<T> recipes) {
        var ii = (IRecipeManagerInternal) internal;
        ii.removeRecipes(recipeType, recipes);
    }
}
