package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@NullMarked
public class RecipeManagerPlugin implements IRecipeManagerPlugin {
    private final IIngredientManager ingredientManager;
    private final EnumMap<RecipeIngredientRole, RecipeMap> recipeMaps = new EnumMap<>(RecipeIngredientRole.class);
    private final Map<RecipeType<?>, List<Buildable<?>>> recipesByCategory = new HashMap<>();

    public RecipeManagerPlugin(IIngredientManager ingredientManager) {
        this.ingredientManager = ingredientManager;
        for (var role : RecipeIngredientRole.values()) {
            recipeMaps.put(Objects.requireNonNull(role), new RecipeMap(role, ingredientManager));
        }
    }

    public <T, R extends Buildable<T> & IIngredientSupplier> void registerCatalyst(ITypedIngredient<?> catalyst, RecipeType<T> type, List<R> recipes) {
        var map = Objects.requireNonNull(recipeMaps.get(RecipeIngredientRole.CATALYST));
        for (var recipe : recipes) {
            map.addRecipe(type, recipe, catalyst);
        }
    }

    public <T, R extends Buildable<T> & IIngredientSupplier> void addRecipe(RecipeType<T> type, R recipe) {
        recipesByCategory.computeIfAbsent(type, i -> new ArrayList<>()).add(recipe);
        for (var role : RecipeIngredientRole.values()) {
            var map = Objects.requireNonNull(recipeMaps.get(role));
            map.addRecipe(type, recipe, recipe);
        }
    }

    @Override
    public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
        var role = focus.getRole();
        var ingredient = focus.getTypedValue();
        System.out.println("getRecipeTypes for " + role + " with " + ingredient);
        var l = Objects.requireNonNull(recipeMaps.get(role)).getRecipeTypes(ingredient);
        System.out.println(l);
        return l;
    }

    @Override
    public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
        var type = recipeCategory.getRecipeType();
        System.out.println("getForType " + recipeCategory.getRecipeType()
                .getUid() + " role " + focus.getRole() + " with " + focus.getTypedValue());
        var stages = stages();
        if (notReady(stages)) return List.of();
        var role = focus.getRole();
        var recipeMap = Objects.requireNonNull(recipeMaps.get(role));
        var ingredient = focus.getTypedValue();
        var recipes = build(recipeMap.getPossibleRecipes(type, ingredient), stages);
        if (recipeMap.isCatalystForCategory(type, ingredient)) {
            var recipesForCategory = getRecipes(recipeCategory);
            recipes = Stream.concat(recipes, recipesForCategory.stream()).distinct();
        }
        return recipes.toList();
    }

    private <T> Stream<T> build(Stream<Buildable<T>> stream, BaseStages stages) {
        return stream.map(s -> s.build(stages)).filter(Builder::canBuild).map(Builder::build);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
        System.out.println("getForType " + recipeCategory.getRecipeType().getUid());
        var stages = stages();
        if (notReady(stages)) return List.of();
        return build(recipesByCategory.getOrDefault(recipeCategory.getRecipeType(), List.of())
                .stream()
                .map(s -> (Buildable<T>) s), stages).toList();
    }

    private boolean notReady(BaseStages stages) {
        return !stages.has(BaseStages.CompileIndex.ATTRIBUTE);
    }

    private BaseStages stages() {
        return Objects.requireNonNull(CommonInstances.platformPlayerProvider.clientSelfPlayer()).getGameStages();
    }
}
