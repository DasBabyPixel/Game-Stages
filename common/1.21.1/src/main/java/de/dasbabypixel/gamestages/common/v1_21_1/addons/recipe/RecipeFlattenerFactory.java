package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener.Flattener;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener.FlattenerFactory;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.function.Predicate.not;

@NullMarked
public class RecipeFlattenerFactory implements FlattenerFactory<CommonRecipeCollection> {
    @Override
    public GameContentType<CommonRecipeCollection> type() {
        return CommonRecipeCollection.TYPE;
    }

    @Override
    public Flattener<CommonRecipeCollection> createUnion() {
        return new Flattener<>() {
            private final Set<ResourceLocation> recipes = new HashSet<>();

            @Override
            public void accept(CommonRecipeCollection list) {
                recipes.addAll(list.recipes());
            }

            @Override
            public CommonRecipeCollection complete() {
                if (recipes.isEmpty()) return CommonRecipeCollection.EMPTY;
                return new CommonRecipeCollection(List.copyOf(recipes));
            }
        };
    }

    @Override
    public Flattener<CommonRecipeCollection> createOnly() {
        return new Flattener<>() {
            private @Nullable Set<ResourceLocation> inclusions;
            private @Nullable List<ResourceLocation> base;

            @Override
            public void accept(CommonRecipeCollection list) {
                if (base == null) {
                    base = list.recipes();
                    inclusions = new HashSet<>();
                } else Objects.requireNonNull(inclusions).addAll(list.recipes());
            }

            @Override
            public CommonRecipeCollection complete() {
                if (base == null) return CommonRecipeCollection.EMPTY;
                Objects.requireNonNull(inclusions);
                return new CommonRecipeCollection(base.stream().filter(inclusions::contains).toList());
            }
        };
    }

    @Override
    public Flattener<CommonRecipeCollection> createExcept() {
        return new Flattener<>() {
            private @Nullable Set<ResourceLocation> exclusions;
            private @Nullable List<ResourceLocation> base;

            @Override
            public void accept(CommonRecipeCollection list) {
                if (base == null) {
                    base = list.recipes();
                    exclusions = new HashSet<>();
                } else Objects.requireNonNull(exclusions).addAll(list.recipes());
            }

            @Override
            public CommonRecipeCollection complete() {
                if (base == null) return CommonRecipeCollection.EMPTY;
                Objects.requireNonNull(exclusions);
                return new CommonRecipeCollection(base.stream().filter(not(exclusions::contains)).toList());
            }
        };
    }
}
