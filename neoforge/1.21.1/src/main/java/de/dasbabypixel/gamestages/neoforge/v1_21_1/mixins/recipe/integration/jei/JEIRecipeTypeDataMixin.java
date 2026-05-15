package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe.integration.jei;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei.IRecipeTypeData;
import mezz.jei.library.recipes.collect.RecipeTypeData;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@NullMarked
@Mixin(value = RecipeTypeData.class, remap = false)
@Implements(@Interface(iface = IRecipeTypeData.class, prefix = "stages_recipe$"))
public class JEIRecipeTypeDataMixin<T> {
    @Shadow
    @Final
    private List<T> recipes;
    @Shadow
    @Final
    private Set<T> hiddenRecipes;

    public void stages_recipe$removeRecipes(Collection<T> recipes) {
        recipes.forEach(this.recipes::remove);
        recipes.forEach(hiddenRecipes::remove);
    }
}
