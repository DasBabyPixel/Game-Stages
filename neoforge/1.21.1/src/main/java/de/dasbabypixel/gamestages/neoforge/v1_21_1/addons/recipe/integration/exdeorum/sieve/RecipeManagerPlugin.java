package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import org.jspecify.annotations.NullMarked;
import thedarkcolour.exdeorum.recipe.sieve.SieveRecipe;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@NullMarked
public class RecipeManagerPlugin implements IRecipeManagerPlugin {
    private final EnumMap<RecipeIngredientRole, RecipeMap> recipeMaps = new EnumMap<>(RecipeIngredientRole.class);
    private final IIngredientManager ingredientManager;
    private final Map<RecipeType<?>, List<JEIUpdatableRecipe<?>>> recipesByCategory = new HashMap<>();

    public RecipeManagerPlugin(IIngredientManager ingredientManager) {
        this.ingredientManager = ingredientManager;
        for (var role : RecipeIngredientRole.values()) {
            recipeMaps.put(Objects.requireNonNull(role), new RecipeMap(role, ingredientManager));
        }
    }

    public <T extends SieveRecipe> void registerCategoryCatalyst(ITypedIngredient<?> catalyst, RecipeType<JEIUpdatableRecipe<T>> type) {
        var map = Objects.requireNonNull(recipeMaps.get(RecipeIngredientRole.CATALYST));
        map.registerCategoryCatalyst(catalyst, type);
    }

    public <T extends SieveRecipe> void registerCatalyst(ITypedIngredient<?> catalyst, RecipeType<JEIUpdatableRecipe<T>> type, List<JEIUpdatableRecipe<T>> recipes) {
        var map = Objects.requireNonNull(recipeMaps.get(RecipeIngredientRole.CATALYST));
        for (var recipe : recipes) {
            map.addRecipe(type, recipe, catalyst);
        }
    }

    public <R extends JEIUpdatableRecipe<?>> void addRecipe(RecipeType<R> type, R recipe, IIngredientSupplier ingredientSupplier) {
        recipesByCategory.computeIfAbsent(type, i -> new ArrayList<>()).add(recipe);
        for (var role : RecipeIngredientRole.values()) {
            var map = Objects.requireNonNull(recipeMaps.get(role));
            map.addRecipe(type, recipe, ingredientSupplier);
        }
    }

    @Override
    public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
        var role = focus.getRole();
        var ingredient = focus.getTypedValue();
        return Objects.requireNonNull(recipeMaps.get(role)).getRecipeTypes(ingredient);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
        var type = recipeCategory.getRecipeType();
        var role = focus.getRole();
        var recipeMap = Objects.requireNonNull(recipeMaps.get(role));
        var ingredient = focus.getTypedValue();
        var str = recipeMap.getPossibleRecipes(type, ingredient);

        if (role == RecipeIngredientRole.OUTPUT) {
            var id = ingredientManager.getIngredientHelper(ingredient.getType()).getUid(ingredient, UidContext.Recipe);
            str = str.filter(r -> {
                // we need to make sure the ingredient is actually unlocked... Otherwise, we would be listed recipes which don't match the requested usage/output.
                var uids = r.recipe().resultIngredientUids();
                return uids.contains(id);
            });
        }
        var recipes = (Stream<T>) str;
        if (recipeMap.isCatalystForCategory(type, ingredient)) {
            var recipesForCategory = getRecipes(recipeCategory);
            recipes = Stream.concat(recipes, recipesForCategory.stream()).distinct();
        }
        return recipes.toList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
        return (List<T>) recipesByCategory.getOrDefault(recipeCategory.getRecipeType(), List.of());
    }

    private boolean notReady(BaseStages stages) {
        return !stages.has(BaseStages.CompileIndex.ATTRIBUTE);
    }

    private BaseStages stages() {
        return Objects.requireNonNull(CommonInstances.platformPlayerProvider.clientSelfPlayer()).getGameStages();
    }
}
