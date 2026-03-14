package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.*;

public class RecipeTree {
    protected final Map<Item, Set<RecipeHolder>> recipesForInput = new HashMap<>();
    protected final Map<Item, Set<RecipeHolder>> recipesForOutput = new HashMap<>();
    protected final Map<Item, Set<RecipeHolder>> related = new HashMap<>();
    protected final HolderLookup.Provider lookup;

    public RecipeTree(RecipeManager recipeManager, HolderLookup.Provider lookup) {
        this.lookup = lookup;
        for (var recipe : recipeManager.getRecipes()) {
            indexRecipe(recipe.id(), recipe.value());
        }
    }

    public Set<RecipeHolder> findRelated(Item item) {
        var s1 = recipesForInput.get(item);
        var s2 = recipesForOutput.get(item);
        if (s1 == null && s2 == null) return Set.of();
        if (s1 == null) return s2;
        if (s2 == null) return s1;
        var s = new HashSet<>(s1);
        s.addAll(s2);
        return Set.copyOf(s);
    }

    protected void indexRecipe(ResourceLocation id, Recipe<?> recipe) {
        var recipeHolder = new RecipeHolder(id, recipe, Objects.hash(id, recipe));
        var ingredients = recipe.getIngredients();
        for (var ingredient : ingredients) {
            indexIngredient(ingredient, recipeHolder);
        }
        indexResult(recipeHolder);
    }

    protected void indexResult(RecipeHolder recipeHolder) {
        var itemStack = recipeHolder.recipe.getResultItem(lookup);
        var item = itemStack.getItem();
        recipesForOutput.computeIfAbsent(item, unused -> new HashSet<>()).add(recipeHolder);
    }

    protected void indexRelated(Item item, RecipeHolder recipeHolder) {

    }

    protected void indexIngredient(Ingredient ingredient, RecipeHolder recipeHolder) {
        var itemStacks = ingredient.getItems();
        for (var itemStack : itemStacks) {
            var item = itemStack.getItem();
            indexItem(item, recipeHolder);
        }
    }

    protected void indexItem(Item item, RecipeHolder recipeHolder) {
        recipesForInput.computeIfAbsent(item, unused -> new HashSet<>()).add(recipeHolder);
    }

    public record RecipeHolder(ResourceLocation id, Recipe<?> recipe, int hash) {
        @Override
        public int hashCode() {
            return hash;
        }
    }
}
