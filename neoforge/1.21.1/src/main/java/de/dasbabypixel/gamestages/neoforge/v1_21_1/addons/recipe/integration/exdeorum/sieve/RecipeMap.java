package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IIngredientManager;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@NullMarked
public class RecipeMap {
    private final RecipeIngredientRole role;
    private final IIngredientManager ingredientManager;
    private final Map<Object, Set<RecipeType<?>>> categoriesByIngredientUidMap = new HashMap<>();
    private final Map<Object, Set<RecipeType<?>>> categoriesByCatalystUidMap = new HashMap<>();
    private final Map<RecipeType<?>, Map<Object, List<Buildable<?>>>> ingredientUidToRecipes = new HashMap<>();

    public RecipeMap(RecipeIngredientRole role, IIngredientManager ingredientManager) {
        this.role = role;
        this.ingredientManager = ingredientManager;
    }

    public <T, R extends Buildable<T>> void addRecipe(RecipeType<T> type, R recipe, ITypedIngredient<?> ingredient) {
        var uid = getIngredientUid(ingredient);
        var map = ingredientUidToRecipes.computeIfAbsent(type, i -> new HashMap<>());
        map.computeIfAbsent(uid, i -> new ArrayList<>()).add(recipe);
        categoriesByIngredientUidMap.computeIfAbsent(uid, i -> new HashSet<>()).add(type);
    }

    @SuppressWarnings("Convert2Diamond")
    public <R extends Buildable<T>, T> void addRecipe(RecipeType<T> type, R recipe, IIngredientSupplier ingredientSupplier) {
        var ingredients = ingredientSupplier.getIngredients(role);
        var uids = new HashSet<Object>();
        for (var ingredient : ingredients) {
            var uid = getIngredientUid(ingredient);
            uids.add(uid);
        }

        if (!uids.isEmpty()) {
            var map = ingredientUidToRecipes.computeIfAbsent(type, i -> new HashMap<>());
            for (var uid : uids) {
                categoriesByIngredientUidMap.computeIfAbsent(uid, i -> new HashSet<>()).add(type);
                map.computeIfAbsent(uid, i -> new ArrayList<>()).add(recipe);
            }
        }
    }

    public <T> List<RecipeType<?>> getRecipeTypes(ITypedIngredient<T> ingredient) {
        var ingredientUid = getIngredientUid(ingredient);
        var categories = categoriesByIngredientUidMap.get(ingredientUid);
        var catalystCategories = categoriesByCatalystUidMap.get(ingredientUid);
        if (categories == null && catalystCategories == null) return List.of();
        if (catalystCategories == null || catalystCategories.isEmpty()) {
            return categories == null ? List.of() : List.copyOf(categories);
        }
        if (categories == null) return List.copyOf(catalystCategories);
        return Stream.concat(categories.stream(), catalystCategories.stream()).toList();
    }

    @SuppressWarnings("unchecked")
    public <T, V> Stream<Buildable<T>> getPossibleRecipes(RecipeType<T> recipeType, ITypedIngredient<V> ingredient) {
        var ingredientUid = getIngredientUid(ingredient);
        var map = ingredientUidToRecipes.get(recipeType);
        if (map == null) return Stream.of();
        var list = map.get(ingredientUid);
        if (list == null) return Stream.of();
        return list.stream().map(s -> (Buildable<T>) s);
    }

    public boolean isCatalystForCategory(RecipeType<?> recipeType, ITypedIngredient<?> ingredient) {
        var ingredientUid = getIngredientUid(ingredient);
        var categories = categoriesByCatalystUidMap.get(ingredientUid);
        return categories != null && categories.contains(recipeType);
    }

    private <T> Object getIngredientUid(ITypedIngredient<T> typedIngredient) {
        IIngredientType<T> type = typedIngredient.getType();
        IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(type);
        return ingredientHelper.getUid(typedIngredient, UidContext.Recipe);
    }
}
