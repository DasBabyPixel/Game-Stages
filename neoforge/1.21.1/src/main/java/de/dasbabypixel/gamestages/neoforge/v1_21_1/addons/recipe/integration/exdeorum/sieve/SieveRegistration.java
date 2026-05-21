package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import thedarkcolour.exdeorum.recipe.sieve.SieveRecipe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

@NullMarked
public class SieveRegistration<T extends SieveRecipe> {
    public final Supplier<net.minecraft.world.item.crafting.RecipeType<T>> mcType;
    public final RecipeType<?> nativeExDeorumType;
    public final RecipeType<JEIUpdatableRecipe<T>> customType;
    public final ItemLike icon;
    public final String translationKey;
    public final MutableInt rows = new MutableInt();
    public final SieveRowVisibility<T> sieveRowVisibility = new SieveRowVisibility<T>(rows);
    public final Supplier<ItemLike[]> sieves;
    public BuildableSieveRecipe.@Nullable Prepared<T> preparedCache;

    @SuppressWarnings("unchecked")
    public SieveRegistration(@Nullable Supplier<net.minecraft.world.item.crafting.RecipeType<T>> mcType, RecipeType<?> nativeExDeorumType, String customType, ItemLike icon, String translationKey, Supplier<List<ItemLike>> sieves) {
        this.mcType = Objects.requireNonNull(mcType);
        this.nativeExDeorumType = nativeExDeorumType;
        this.customType = RecipeType.create("exdeorum", "stages_" + customType, (Class<JEIUpdatableRecipe<T>>) (Object) JEIUpdatableRecipe.class);
        this.icon = icon;
        this.translationKey = translationKey;
        this.sieves = () -> sieves.get().toArray(ItemLike[]::new);
    }

    public void reset() {
        rows.setValue(0);
        sieveRowVisibility.reset();
    }

    public static class SieveRowVisibility<T extends SieveRecipe> {
        private final NavigableMap<Integer, Set<BuildableSieveRecipe<T>>> recipesByRowCount = new TreeMap<>();
        private final Map<BuildableSieveRecipe<T>, Integer> rowCountByRecipe = new HashMap<>();
        private final MutableInt rows;
        private int maxRowCount = 0;

        public SieveRowVisibility(MutableInt rows) {
            this.rows = rows;
        }

        public void reset() {
            recipesByRowCount.clear();
            rowCountByRecipe.clear();
            maxRowCount = 0;
        }

        public void update(BuildableSieveRecipe<T> recipe, int newRowCount) {
            var oldRowCount = rowCountByRecipe.get(recipe);
            if (oldRowCount != null) {
                if (oldRowCount == newRowCount) return;
                recipesByRowCount.compute(oldRowCount, (c, s) -> {
                    Objects.requireNonNull(s).remove(recipe);
                    return s.isEmpty() ? null : s;
                });
                recipesByRowCount.computeIfAbsent(newRowCount, i -> new HashSet<>()).add(recipe);
            } else {
                recipesByRowCount.computeIfAbsent(newRowCount, i -> new HashSet<>()).add(recipe);
            }
            rowCountByRecipe.put(recipe, newRowCount);
        }

        public boolean updateMaxRowCount() {
            var newMaxRowCount = recipesByRowCount.isEmpty() ? 0 : recipesByRowCount.lastKey();
            if (maxRowCount == newMaxRowCount) return false;
            maxRowCount = newMaxRowCount;
            rows.setValue(newMaxRowCount);
            // TODO more update?
            return true;
        }
    }
}
