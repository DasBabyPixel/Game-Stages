package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jspecify.annotations.NullMarked;
import thedarkcolour.exdeorum.recipe.RecipeUtil;

import java.util.List;

@NullMarked
public class JEISieveRecipe {
    public static final RecipeType<JEISieveRecipe> RECIPE_TYPE = RecipeType.create("exdeorum", "stages_sieve", JEISieveRecipe.class);
    public static final MutableInt SIEVE_ROWS = new MutableInt();
    private final Ingredient ingredient;
    private final ItemStack mesh;
    private final List<Result> results;

    public JEISieveRecipe(Ingredient ingredient, ItemStack mesh, List<Result> results) {
        this.ingredient = ingredient;
        this.mesh = mesh;
        this.results = results;
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public ItemStack mesh() {
        return mesh;
    }

    public List<Result> results() {
        return results;
    }


    private static <T> RecipeType<T> recipeType(String path, Class<? extends T> type) {
        return RecipeType.create("exdeorum", "stages_" + path, type);
    }

    public static class Result {
        private final RecipeHolder<?> holder;
        private final ItemStack item;
        private final NumberProvider provider;
        private final boolean byHandOnly;
        private final double expectedCount;

        public Result(RecipeHolder<?> holder, ItemStack item, NumberProvider provider, boolean byHandOnly) {
            this.holder = holder;
            this.item = item;
            this.provider = provider;
            this.byHandOnly = byHandOnly;
            this.expectedCount = RecipeUtil.getExpectedValue(this.provider);
        }

        public ItemStack item() {
            return item;
        }

        public NumberProvider provider() {
            return provider;
        }

        public boolean byHandOnly() {
            return byHandOnly;
        }

        public double expectedCount() {
            return expectedCount;
        }

        public RecipeHolder<?> holder() {
            return holder;
        }
    }
}
