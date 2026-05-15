package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe.integration.jei;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei.IRecipeManagerInternal;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei.IRecipeMap;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.jei.IRecipeTypeData;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.library.recipes.RecipeManagerInternal;
import mezz.jei.library.recipes.collect.RecipeMap;
import mezz.jei.library.recipes.collect.RecipeTypeDataMap;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.EnumMap;
import java.util.List;

@NullMarked
@Mixin(value = RecipeManagerInternal.class, remap = false)
@Implements(@Interface(iface = IRecipeManagerInternal.class, prefix = "stages_recipe$"))
@SuppressWarnings("unchecked")
public class JEIRecipeManagerInternalMixin {
    @Shadow
    @Final
    private EnumMap<RecipeIngredientRole, RecipeMap> recipeMaps;
    @Shadow
    @Final
    private RecipeTypeDataMap recipeTypeDataMap;
    @Shadow
    @Nullable
    @Unmodifiable
    private List<IRecipeCategory<?>> recipeCategoriesVisibleCache;

    public <T> void stages_recipe$removeRecipes(RecipeType<T> recipeType, List<T> recipes) {
        var recipeTypeData = recipeTypeDataMap.get(recipeType);
        var irecipeTypeData = (IRecipeTypeData<T>) recipeTypeData;
        irecipeTypeData.removeRecipes(recipes);
        var recipeCategory = recipeTypeData.getRecipeCategory();

        for (T recipe : recipes) {
            stages_recipeI$removeRecipe(recipeCategory, recipe);
        }

        recipeCategoriesVisibleCache = null;
    }

    @Unique
    private <T> void stages_recipeI$removeRecipe(IRecipeCategory<T> recipeCategory, T recipe) {
        var recipeType = recipeCategory.getRecipeType();

        if (!recipeCategory.isHandled(recipe)) {
            return;
        }
        for (var recipeMap : recipeMaps.values()) {
            var imap = (IRecipeMap) recipeMap;
            imap.removeRecipe(recipeType, recipe);
        }
    }
}
