package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener.Flattener;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener.FlattenerFactory;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.function.Predicate.not;

public class RecipeFlattenerFactory implements FlattenerFactory<CommonRecipeCollection> {
    @Override
    public @NonNull GameContentType<CommonRecipeCollection> type() {
        return CommonRecipeCollection.TYPE;
    }

    @Override
    public @NonNull Flattener<CommonRecipeCollection> createUnion() {
        return new Flattener<>() {
            private final Set<ResourceLocation> recipes = new HashSet<>();

            @Override
            public void accept(@NonNull CommonRecipeCollection list) {
                recipes.addAll(list.recipes());
            }

            @Override
            public @NonNull CommonRecipeCollection complete() {
                if (recipes.isEmpty()) return CommonRecipeCollection.EMPTY;
                return new CommonRecipeCollection(List.copyOf(recipes));
            }
        };
    }

    @Override
    public @NonNull Flattener<CommonRecipeCollection> createOnly() {
        return new Flattener<>() {
            private Set<ResourceLocation> inclusions;
            private List<ResourceLocation> base;

            @Override
            public void accept(@NonNull CommonRecipeCollection list) {
                if (base == null) {
                    base = list.recipes();
                    inclusions = new HashSet<>();
                } else inclusions.addAll(list.recipes());
            }

            @Override
            public @NonNull CommonRecipeCollection complete() {
                if (base == null) return CommonRecipeCollection.EMPTY;
                return new CommonRecipeCollection(base.stream().filter(inclusions::contains).toList());
            }
        };
    }

    @Override
    public @NonNull Flattener<CommonRecipeCollection> createExcept() {
        return new Flattener<>() {
            private Set<ResourceLocation> exclusions;
            private List<ResourceLocation> base;

            @Override
            public void accept(@NonNull CommonRecipeCollection list) {
                if (base == null) {
                    base = list.recipes();
                    exclusions = new HashSet<>();
                } else exclusions.addAll(list.recipes());
            }

            @Override
            public @NonNull CommonRecipeCollection complete() {
                if (base == null) return CommonRecipeCollection.EMPTY;
                return new CommonRecipeCollection(base.stream().filter(not(exclusions::contains)).toList());
            }
        };
    }
}
