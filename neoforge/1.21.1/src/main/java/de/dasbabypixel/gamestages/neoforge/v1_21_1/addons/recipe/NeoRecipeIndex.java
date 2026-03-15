package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.RecipeIndex;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.RecipeManager;

public class NeoRecipeIndex extends RecipeIndex {
    public NeoRecipeIndex(RecipeManager recipeManager, VRecipeAddon recipeAddon, AbstractGameStageManager manager, HolderLookup.Provider lookup) {
        super(recipeManager, recipeAddon, manager, lookup);
    }
//    @Override
//    protected void indexIngredient(Ingredient ingredient, RecipeHolder recipeHolder) {
//        if (ingredient.isCustom()) {
//            var custom = ingredient.getCustomIngredient();
//            switch (custom) {
//                case CompoundIngredient(List<Ingredient> children) -> {
//                    for (var child : children) {
//                        indexIngredient(child, recipeHolder);
//                    }
//                }
//                case DifferenceIngredient difference ->
//                        difference.getItems().map(ItemStack::getItem).forEach(i -> indexItem(i, recipeHolder));
//                case null, default ->
//                        System.err.println(recipeHolder.id() + " Custom Ingredient: " + ingredient.getCustomIngredient());
//            }
//        } else {
//            super.indexIngredient(ingredient, recipeHolder);
//        }
//    }
}
