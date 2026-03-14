package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.RecipeTree;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;

import java.util.List;

public class NeoRecipeTree extends RecipeTree {
    public NeoRecipeTree(RecipeManager recipeManager, HolderLookup.Provider lookup) {
        super(recipeManager, lookup);
    }

    @Override
    protected void indexIngredient(Ingredient ingredient, RecipeHolder recipeHolder) {
        if (ingredient.isCustom()) {
            var custom = ingredient.getCustomIngredient();
            switch (custom) {
                case CompoundIngredient(List<Ingredient> children) -> {
                    for (var child : children) {
                        indexIngredient(child, recipeHolder);
                    }
                }
                case DifferenceIngredient difference ->
                        difference.getItems().map(ItemStack::getItem).forEach(i -> indexItem(i, recipeHolder));
                case null, default ->
                        System.err.println(recipeHolder.id() + " Custom Ingredient: " + ingredient.getCustomIngredient());
            }
        } else {
            super.indexIngredient(ingredient, recipeHolder);
        }
    }
}
