package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei.Context;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei.RecipeAndType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei.RecipeConverter;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jspecify.annotations.NullMarked;
import thedarkcolour.exdeorum.recipe.sieve.SieveRecipe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class SieveConverter<T extends SieveRecipe> implements RecipeConverter<T, RecipeInput, SieveConverter.Cache<T>> {
    private final SieveRegistration<T> registration;

    public SieveConverter(SieveRegistration<T> registration) {
        this.registration = registration;
    }

    private static int requiredRowCount(int visibleCount) {
        return (visibleCount + 8) / 9;
    }

    private static int computeVisibleCount(BaseStages stages, BuildableSieveRecipe<?> recipe) {
        int visibleCount = 0;
        for (var possibleResult : recipe.possibleResults()) {
            var compiled = VRecipeAddon.getEntry(stages, possibleResult.holder());
            var visible = compiled == null || compiled.predicate().test();
            if (visible) visibleCount++;
        }
        return visibleCount;
    }

    @Override
    public void convert(Context context, List<RecipeAndType<?>> list, net.minecraft.world.item.crafting.RecipeType<T> minecraftType, List<RecipeHolder<T>> recipeHolders, Cache<T> cache) {
        var recipes = new HashSet<BuildableSieveRecipe<T>>();
        for (var recipeHolder : recipeHolders) {
            var recipe = Objects.requireNonNull(cache.jeiRecipeByHolder.get(recipeHolder));
            recipes.add(recipe);
        }
        for (var recipe : recipes) {
            list.add(new RecipeAndType<>(registration.customType, recipe.updatable()));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void invalidate(List<RecipeAndType<?>> list) {
        var stages = ClientGameStageManager.stages();
        for (var recipeAndType : list) {
            var updatable = (JEIUpdatableRecipe<T>) recipeAndType.recipe();
            updatable.clearCache();
            var recipe = updatable.buildable();
            var visibleCount = computeVisibleCount(stages, recipe);
            var rowCount = requiredRowCount(visibleCount);
            registration.sieveRowVisibility.update(recipe, rowCount);
        }
        registration.sieveRowVisibility.updateMaxRowCount();
    }

    @Override
    public Cache<T> buildCache(Context context, net.minecraft.world.item.crafting.RecipeType<T> minecraftType) {
        var jeiRecipeByHolder = new HashMap<RecipeHolder<T>, BuildableSieveRecipe<T>>();
        var prepared = Objects.requireNonNull(registration.preparedCache);
        registration.preparedCache = null;

        for (var sieveRecipe : prepared.sieveRecipes()) {
            for (var possibleResult : sieveRecipe.possibleResults()) {
                jeiRecipeByHolder.put(possibleResult.holder(), sieveRecipe);
            }
        }

//        for (var sieveRecipe : prepared.sieveRecipes()) {
//            recipeManagerPlugin.addRecipe(registration.customType, sieveRecipe.updatable(), sieveRecipe);
//        }
//        for (var sieve : registration.sieves.get()) {
//            var stack = Objects.requireNonNull(sieve).asItem().getDefaultInstance();
//            var ingredient = Objects.requireNonNull(TypedIngredient.createAndFilterInvalid(ingredientManager, VanillaTypes.ITEM_STACK, stack, true));
//            recipeManagerPlugin.registerCatalyst(ingredient, registration.customType, prepared.sieveRecipes());
//        }

        return new Cache<>(jeiRecipeByHolder);
    }

    public record Cache<T extends SieveRecipe>(Map<RecipeHolder<T>, BuildableSieveRecipe<T>> jeiRecipeByHolder) {
        public Cache {
            jeiRecipeByHolder = Map.copyOf(jeiRecipeByHolder);
        }
    }
}
