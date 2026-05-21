package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import thedarkcolour.exdeorum.recipe.RecipeUtil;
import thedarkcolour.exdeorum.recipe.sieve.SieveRecipe;

import java.util.List;
import java.util.Set;

@NullMarked
public class JEISieveRecipe<T extends SieveRecipe> {
    private final @Nullable ResourceLocation identifier;
    private final Ingredient ingredient;
    private final ItemStack mesh;
    private final Set<Object> resultIngredientUids;
    private final List<Result<T>> results;

    public JEISieveRecipe(@Nullable ResourceLocation identifier, Ingredient ingredient, ItemStack mesh, Set<Object> resultIngredientUids, List<Result<T>> results) {
        this.identifier = identifier;
        this.ingredient = ingredient;
        this.mesh = mesh;
        this.resultIngredientUids = resultIngredientUids;
        this.results = results;
    }

    public Set<Object> resultIngredientUids() {
        return resultIngredientUids;
    }

    public @Nullable ResourceLocation identifier() {
        return identifier;
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public ItemStack mesh() {
        return mesh;
    }

    public List<Result<T>> results() {
        return results;
    }

    public static class Result<T extends SieveRecipe> {
        private final RecipeHolder<T> holder;
        private final ItemStack item;
        private final NumberProvider provider;
        private final boolean byHandOnly;
        private final double expectedCount;

        public Result(RecipeHolder<T> holder, ItemStack item, NumberProvider provider, boolean byHandOnly) {
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

        public RecipeHolder<T> holder() {
            return holder;
        }
    }
}
