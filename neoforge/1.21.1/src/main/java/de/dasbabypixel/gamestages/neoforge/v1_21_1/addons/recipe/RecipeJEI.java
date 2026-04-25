package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionEntry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class RecipeJEI implements NeoAddonJEI {
    public static @Nullable RecipeManager recipeManager;
    private @Nullable IJeiRuntime runtime;

    @Override
    public void singleRefreshAll(AbstractGameStageManager<?> instance, BaseStages stages) {
        var collected = new Collected();
        iterate(stages, CommonRecipeCollection.TYPE, entry -> {
            if (entry instanceof CommonRecipeRestrictionEntry.Compiled(
                    var preCompiled, var predicate, var hideInJEI
            )) {
                if (!hideInJEI) return;

                var show = predicate.test();
                collected.collect(show, preCompiled.gameContent());
            }
        });

        Objects.requireNonNull(runtime);
        collected.apply(runtime);
    }

    @Override
    public void postCompileAll(AbstractGameStageManager<?> instance, BaseStages stages) {
        iterate(stages, CommonRecipeCollection.TYPE, entry -> {
            if (entry instanceof CommonRecipeRestrictionEntry.Compiled(
                    var preCompiled, var predicate, var hideInJEI
            )) {
                if (!hideInJEI) return;
                predicate.addNotifier(newTest -> {
                    var collected = new Collected();
                    collected.collect(newTest, preCompiled.gameContent());
                    collected.apply(Objects.requireNonNull(runtime));
                });
            }
        });
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onRuntimeUnavailable() {
        this.runtime = null;
    }

    private static class Collected {
        private final RecipeManager recipeManager = Objects.requireNonNull(RecipeJEI.recipeManager);
        private final Map<RecipeType<?>, Holder<Recipe<?>>> showCache = new HashMap<>();
        private final Map<RecipeType<?>, Holder<Recipe<?>>> hideCache = new HashMap<>();

        @SuppressWarnings("unchecked")
        public void collect(boolean show, CommonRecipeCollection recipes) {
            var recipeIds = recipes.recipes();
            for (var recipeId : recipeIds) {
                var recipe = recipeManager.byKey(recipeId).orElseThrow();
                var showList = showCache.computeIfAbsent(recipe.value()
                        .getType(), type -> new Holder<>((RecipeType<Recipe<?>>) type, new ArrayList<>()));
                var hideList = hideCache.computeIfAbsent(recipe.value()
                        .getType(), type -> new Holder<>((RecipeType<Recipe<?>>) type, new ArrayList<>()));

                if (show) {
                    showList.recipes.add((RecipeHolder<Recipe<?>>) recipe);
                } else {
                    hideList.recipes.add((RecipeHolder<Recipe<?>>) recipe);
                }
            }
        }

        public void apply(IJeiRuntime runtime) {
            for (var holder : showCache.values()) {
                holder.unhide(runtime.getRecipeManager());
            }
            for (var holder : hideCache.values()) {
                holder.hide(runtime.getRecipeManager());
            }
        }
    }

    private record Holder<T extends Recipe<?>>(RecipeType<T> type, List<RecipeHolder<T>> recipes) {
        @SuppressWarnings("unchecked")
        private void unhide(IRecipeManager recipeManager) {
            var key = Objects.requireNonNull(BuiltInRegistries.RECIPE_TYPE.getKey(type));
            var jeiType = (mezz.jei.api.recipe.RecipeType<RecipeHolder<T>>) recipeManager.getRecipeType(key)
                    .orElseThrow();
            recipeManager.unhideRecipes(jeiType, recipes);
        }

        @SuppressWarnings("unchecked")
        private void hide(IRecipeManager recipeManager) {
            var key = Objects.requireNonNull(BuiltInRegistries.RECIPE_TYPE.getKey(type));
            var jeiType = (mezz.jei.api.recipe.RecipeType<RecipeHolder<T>>) recipeManager.getRecipeType(key)
                    .orElseThrow();
            recipeManager.hideRecipes(jeiType, recipes);
        }
    }
}
