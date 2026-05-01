package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionEntry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class RecipeJEI implements NeoAddonJEI {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecipeJEI.class);
    public static @Nullable RecipeManager recipeManager;
    private @Nullable IJeiRuntime runtime;

    @Override
    public void singleRefreshAll(AbstractGameStageManager<?> instance, BaseStages stages) {
        var collected = new Collected(Objects.requireNonNull(runtime));
        iterate(stages, CommonRecipeCollection.TYPE, entry -> {
            if (entry instanceof CommonRecipeRestrictionEntry.Compiled(
                    var preCompiled, var predicate, var hideInJEI
            )) {
                if (!hideInJEI) return;

                var show = predicate.test();
                collected.collect(show, preCompiled.gameContent());
            }
        });

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
                    var collected = new Collected(Objects.requireNonNull(runtime));
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
        private final IJeiRuntime runtime;
        private final RecipeManager recipeManager = Objects.requireNonNull(RecipeJEI.recipeManager);
        private final Map<mezz.jei.api.recipe.RecipeType<?>, Holder<?>> showCache = new HashMap<>();
        private final Map<mezz.jei.api.recipe.RecipeType<?>, Holder<?>> hideCache = new HashMap<>();

        public Collected(IJeiRuntime runtime) {
            this.runtime = runtime;
        }

        @SuppressWarnings("unchecked")
        public void collect(boolean show, CommonRecipeCollection recipes) {
            var recipeIds = recipes.recipes();

            var cache = new HashMap<net.minecraft.world.item.crafting.RecipeType<?>, List<RecipeHolder<?>>>();

            for (var recipeId : recipeIds) {
                var recipe = recipeManager.byKey(recipeId).orElseThrow();
                var type = recipe.value().getType();
                cache.computeIfAbsent(type, ignored -> new ArrayList<>()).add(recipe);
            }
            for (var entry : cache.entrySet()) {
                Objects.requireNonNull(entry);
                var type = entry.getKey();

                var jeiType = runtime.getRecipeManager()
                        .getRecipeType(Objects.requireNonNull(BuiltInRegistries.RECIPE_TYPE.getKey(type)))
                        .orElse(null);
                if (jeiType == null) {
                    LOGGER.error("Skipping unknown type {}", BuiltInRegistries.RECIPE_TYPE.getKey(type));
                    return;
                }
                var recipeClass = jeiType.getRecipeClass();
                var recipeList = new ArrayList<>();
                for (var recipeHolder : entry.getValue()) {
                    if (recipeClass.isInstance(recipeHolder)) {
                        recipeList.add(recipeHolder);
                    } else if (recipeClass.isInstance(recipeHolder.value())) {
                        recipeList.add(recipeHolder.value());
                    } else if (recipeClass.isInstance(recipeHolder.id())) {
                        recipeList.add(recipeHolder.id());
                    } else {
                        recipeList.clear();
                        LOGGER.error("Failed to convert recipe holder to instance of {}, skipping recipe", recipeClass.getName());
                        break;
                    }
                }

                var showList = (Holder<@NonNull Object>) showCache.computeIfAbsent(jeiType, t -> new Holder<>(t, new ArrayList<>()));
                var hideList = (Holder<@NonNull Object>) hideCache.computeIfAbsent(jeiType, t -> new Holder<>(t, new ArrayList<>()));

                for (var o : recipeList) {
                    if (show) {
                        showList.recipes.add(Objects.requireNonNull(o));
                    } else {
                        hideList.recipes.add(Objects.requireNonNull(o));
                    }
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

    private record Holder<T>(mezz.jei.api.recipe.RecipeType<T> type, List<T> recipes) {
        private void unhide(IRecipeManager recipeManager) {
            recipeManager.unhideRecipes(type, recipes);
        }

        private void hide(IRecipeManager recipeManager) {
            recipeManager.hideRecipes(type, recipes);
        }
    }
}
