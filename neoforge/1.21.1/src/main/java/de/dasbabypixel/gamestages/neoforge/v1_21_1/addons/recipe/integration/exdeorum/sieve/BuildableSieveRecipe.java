package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.ingredients.TypedIngredient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import thedarkcolour.exdeorum.client.ClientsideCode;
import thedarkcolour.exdeorum.recipe.RecipeUtil;
import thedarkcolour.exdeorum.recipe.sieve.SieveRecipe;
import thedarkcolour.exdeorum.registry.EItems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public class BuildableSieveRecipe<T extends SieveRecipe> implements IIngredientSupplier {
    private final IIngredientManager ingredientManager;
    private final @Nullable ResourceLocation identifier;
    private final Ingredient ingredient;
    private final ItemStack mesh;
    private final List<JEISieveRecipe.Result<T>> possibleResults;
    private final JEIUpdatableRecipe<T> updatable = new JEIUpdatableRecipe<>(this);

    public BuildableSieveRecipe(IIngredientManager ingredientManager, @Nullable ResourceLocation identifier, Ingredient ingredient, ItemStack mesh, List<JEISieveRecipe.Result<T>> results) {
        this.ingredientManager = ingredientManager;
        this.identifier = identifier;
        this.ingredient = ingredient;
        this.mesh = mesh;
        this.possibleResults = results;
    }

    public static <T extends SieveRecipe> Prepared<T> prepareAll(IIngredientManager ingredientManager, net.minecraft.world.item.crafting.RecipeType<T> recipeType) {
        int maxSieveRows = 1;

        var recipeManager = Objects.requireNonNull(ClientsideCode.getRecipeManager());

        var recipes = new ArrayList<RecipeEntry<T>>();
        for (var holder : recipeManager.getAllRecipesFor(recipeType)) {
            Objects.requireNonNull(holder);
            var recipe = holder.value();
            recipes.add(new RecipeEntry<>(holder, recipe));
        }

        Multimap<Ingredient, RecipeEntry<T>> ingredientGrouper = ArrayListMultimap.create();

        for (int i = 0; i < recipes.size(); i++) {
            var recipe = Objects.requireNonNull(recipes.get(i));

            ingredientGrouper.put(recipe.recipe().ingredient(), recipe);

            for (int j = i + 1; j < recipes.size(); j++) {
                var other = Objects.requireNonNull(recipes.get(j));

                if (RecipeUtil.areIngredientsEqual(recipe.recipe().ingredient(), other.recipe().ingredient())) {
                    ingredientGrouper.put(recipe.recipe().ingredient(), other);
                    recipes.remove(other);
                    j--;
                }
            }
        }

        List<BuildableSieveRecipe<T>> jeiRecipes = new ArrayList<>();
        // Sort based on expected count of result
        var resultSorter = Objects
                .requireNonNull(Comparator.<JEISieveRecipe.Result<T>>comparingDouble(JEISieveRecipe.Result::expectedCount))
                .reversed();
        // Sort based on order of sieve tier
        var meshSorter = Comparator.<Item>comparingInt(BuildableSieveRecipe::meshOrder);

        // ingredients with common ingredients are grouped into lists (ex. dirt)
        for (var ingredient : ingredientGrouper.keySet()) {
            Multimap<Item, RecipeEntry<T>> meshGrouper = ArrayListMultimap.create();
            var values = ingredientGrouper.get(ingredient);

            // these lists are grouped into sub lists based on their meshes (ex. dirt with string mesh)
            for (var recipe : values) {
                for (var stack : Objects.requireNonNull(recipe.recipe().mesh).getItems()) {
                    meshGrouper.put(Objects.requireNonNull(stack).getItem(), recipe);
                }
            }

            // the sub lists have their results combined for displaying in JEI
            var meshes = new ArrayList<>(meshGrouper.keySet());
            meshes.sort(meshSorter);

            for (var mesh : meshes) {
                var meshRecipes = meshGrouper.get(mesh);
                var results = new ArrayList<JEISieveRecipe.Result<T>>(meshRecipes.size());

                var hash = meshRecipes.stream().map(s -> s.holder.id()).collect(Collectors.toSet()).hashCode();
                var identifier = ResourceLocation.fromNamespaceAndPath("gamestages", Integer.toHexString(hash));

                for (var recipeEntry : meshRecipes) {
                    var recipe = recipeEntry.recipe();
                    var holder = recipeEntry.holder();
                    int resultCount = recipe.resultAmount instanceof ConstantValue(float value) ? Math.round(value) : 1;
                    results.add(new JEISieveRecipe.Result<>(holder, Objects
                            .requireNonNull(recipe.result)
                            .copyWithCount(resultCount), Objects.requireNonNull(recipe.resultAmount), recipe.byHandOnly));
                }

                results.sort(resultSorter);

                var jeiRecipe = new BuildableSieveRecipe<>(ingredientManager, identifier, ingredient, new ItemStack(mesh), results);
                jeiRecipes.add(jeiRecipe);

                var rows = Mth.ceil((float) meshRecipes.size() / 9f);
                if (rows > maxSieveRows) {
                    maxSieveRows = rows;
                }
            }
        }

        return new Prepared<>(jeiRecipes);
    }

    private static int meshOrder(Item mesh) {
        if (mesh == Objects.requireNonNull(EItems.STRING_MESH).get()) {
            return -5;
        } else if (mesh == Objects.requireNonNull(EItems.FLINT_MESH).get()) {
            return -4;
        } else if (mesh == Objects.requireNonNull(EItems.IRON_MESH).get()) {
            return -3;
        } else if (mesh == Objects.requireNonNull(EItems.GOLDEN_MESH).get()) {
            return -2;
        } else if (mesh == Objects.requireNonNull(EItems.DIAMOND_MESH).get()) {
            return -1;
        } else if (mesh == Objects.requireNonNull(EItems.NETHERITE_MESH).get()) {
            return 0;
        } else {
            return BuiltInRegistries.ITEM.getId(mesh);
        }
    }

    public JEIUpdatableRecipe<T> updatable() {
        return updatable;
    }

    public @Nullable ResourceLocation identifier() {
        return identifier;
    }

    public ItemStack mesh() {
        return mesh;
    }

    public List<JEISieveRecipe.Result<T>> possibleResults() {
        return possibleResults;
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public ABuilder build(BaseStages stages) {
        return new ABuilder(stages);
    }

    @Override
    public List<ITypedIngredient<?>> getIngredients(RecipeIngredientRole role) {
        if (role == RecipeIngredientRole.OUTPUT) {
            var l = new ArrayList<ITypedIngredient<?>>();
            for (var possibleResult : possibleResults) {
                var stack = possibleResult.item();
                var ingredient = Objects.requireNonNull(TypedIngredient.createAndFilterInvalid(ingredientManager, VanillaTypes.ITEM_STACK, stack, true));
                l.add(ingredient);
            }
            return l;
        } else if (role == RecipeIngredientRole.CATALYST) {
            ITypedIngredient<ItemStack> ingredient = Objects.requireNonNull(TypedIngredient.createAndFilterInvalid(ingredientManager, VanillaTypes.ITEM_STACK, mesh, true));
            return List.of(ingredient);
        } else if (role == RecipeIngredientRole.INPUT) {
            var l = new ArrayList<ITypedIngredient<?>>();
            for (var item : ingredient.getItems()) {
                var ingredient = Objects.requireNonNull(TypedIngredient.createAndFilterInvalid(ingredientManager, VanillaTypes.ITEM_STACK, item, true));
                l.add(ingredient);
            }
            return l;
        }
        return List.of();
    }

    public record Prepared<T extends SieveRecipe>(List<BuildableSieveRecipe<T>> sieveRecipes) {
        public Prepared {
            sieveRecipes = List.copyOf(sieveRecipes);
        }
    }

    private record RecipeEntry<T extends SieveRecipe>(RecipeHolder<T> holder, SieveRecipe recipe) {
    }

    public class ABuilder {
        private final List<JEISieveRecipe.Result<T>> actualResults;
        private final Set<Object> resultIngredientUids;

        public ABuilder(BaseStages stages) {
            var stackHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
            var actualResults = new ArrayList<JEISieveRecipe.Result<T>>();
            var resultIngredientUids = new HashSet<>();
            var ready = stages.has(BaseStages.CompileIndex.ATTRIBUTE);
            for (var possibleResult : possibleResults) {
                if (ready) {
                    var entry = VRecipeAddon.getEntry(stages, possibleResult.holder());
                    if (entry != null && !entry.predicate().test()) {
                        // Recipe locked
                        continue;
                    }
                }
                actualResults.add(possibleResult);
                resultIngredientUids.add(stackHelper.getUid(possibleResult.item(), UidContext.Recipe));
            }
            this.actualResults = List.copyOf(actualResults);
            this.resultIngredientUids = Set.copyOf(resultIngredientUids);
        }

        public boolean canBuild() {
            return !actualResults.isEmpty();
        }

        public JEISieveRecipe<T> build() {
            return new JEISieveRecipe<>(identifier, ingredient, mesh, resultIngredientUids, actualResults);
        }
    }
}
