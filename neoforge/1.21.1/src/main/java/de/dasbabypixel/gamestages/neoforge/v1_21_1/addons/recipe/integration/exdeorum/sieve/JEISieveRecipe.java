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

@NullMarked
public class JEISieveRecipe {
    private final @Nullable ResourceLocation identifier;
    private final Ingredient ingredient;
    private final ItemStack mesh;
    private final List<Result> results;

    public JEISieveRecipe(@Nullable ResourceLocation identifier, Ingredient ingredient, ItemStack mesh, List<Result> results) {
        this.identifier = identifier;
        this.ingredient = ingredient;
        this.mesh = mesh;
        this.results = results;
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

    public List<Result> results() {
        return results;
    }

    public static class Result {
        private final RecipeHolder<? extends SieveRecipe> holder;
        private final ItemStack item;
        private final NumberProvider provider;
        private final boolean byHandOnly;
        private final double expectedCount;

        public Result(RecipeHolder<? extends SieveRecipe> holder, ItemStack item, NumberProvider provider, boolean byHandOnly) {
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

        public RecipeHolder<? extends SieveRecipe> holder() {
            return holder;
        }
    }
}
