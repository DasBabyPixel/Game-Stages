package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.addon.AddonManager;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.And;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.Or;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.True;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.messages.RecipeMessages;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.messages.ResolveItemStackPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public final class RecipeIndex {
    private final AbstractGameStageManager<?> manager;
    private final Map<ResourceLocation, Entry> recipeEntries = new HashMap<>();
    //    protected final Map<Item, Set<RecipeHolder>> recipesForInput = new HashMap<>();
//    protected final Map<Item, Set<RecipeHolder>> recipesForOutput = new HashMap<>();
//    protected final Map<Item, Set<RecipeHolder>> related = new HashMap<>();
    private final HolderLookup.Provider lookup;
    private final VRecipeAddon recipeAddon;

    public RecipeIndex(RecipeManager recipeManager, VRecipeAddon recipeAddon, AbstractGameStageManager<?> manager, HolderLookup.Provider lookup) {
        this.recipeAddon = recipeAddon;
        this.manager = manager;
        this.lookup = lookup;
        for (var recipe : recipeManager.getRecipes()) {
            Objects.requireNonNull(recipe);
            indexRecipe(recipe.id(), recipe.value());
        }
    }

    public PreparedRestrictionPredicate getImplicitDependencies(ResourceLocation id) {
        return Objects.requireNonNull(recipeEntries.get(id)).implicitPredicate();
    }

    private void index(RecipeHolder holder, Entry entry) {
        recipeEntries.put(holder.id, entry);
    }

    //    public Set<RecipeHolder> findRelated(Item item) {
//        var s1 = recipesForInput.get(item);
//        var s2 = recipesForOutput.get(item);
//        if (s1 == null && s2 == null) return Set.of();
//        if (s1 == null) return s2;
//        if (s2 == null) return s1;
//        var s = new HashSet<>(s1);
//        s.addAll(s2);
//        return Set.copyOf(s);
//    }
//
    private void indexRecipe(ResourceLocation id, Recipe<?> recipe) {
        var recipeHolder = new RecipeHolder(id, recipe, Objects.hash(id, recipe));
        indexRecipe(recipeHolder);
//        var ingredients = recipe.getIngredients();
//        for (var ingredient : ingredients) {
//            indexIngredient(ingredient, recipeHolder);
//        }
//        indexResult(recipeHolder);
    }

    private void indexRecipe(RecipeHolder holder) {
        var andList = new ArrayList<PreparedRestrictionPredicate>();
        for (var ingredient : holder.recipe().getIngredients()) {
            Objects.requireNonNull(ingredient);
            if (holder.id.equals(ResourceLocation.parse("minecraft:oak_planks"))) {
                for (var item : ingredient.getItems()) {
                    System.out.println(item + " -> " + resolveItemStackPredicate(item, ingredient));
                }
            }
            andList.add(resolveIngredientPredicate(ingredient));
        }
        if (holder.id.equals(ResourceLocation.parse("minecraft:oak_planks"))) System.out.println(andList);
        var ingredientPredicate = And.INSTANCE.prepare(andList);
        var resultItem = holder.recipe().getResultItem(lookup);
        var resultPredicate = resolveItemStackPredicate(resultItem, null);

        var implicitPredicate = And.INSTANCE.prepare(List.of(ingredientPredicate, resultPredicate));

        var entry = new Entry(implicitPredicate);
        index(holder, entry);
    }

    // Ingredient -> Predicate

    @SuppressWarnings("DataFlowIssue")
    private PreparedRestrictionPredicate resolveItemStackPredicate(ItemStack itemStack, @Nullable Ingredient ingredient) {
        var msg = AddonManager
                .instance()
                .sendMessage(recipeAddon, RecipeMessages.ORIGIN_ID, ResolveItemStackPredicate.ID, ResolveItemStackPredicate::new, itemStack, ingredient);
        return msg == null ? True.INSTANCE.prepare() : msg.predicate;
    }

    private PreparedRestrictionPredicate resolveIngredientPredicate(Ingredient ingredient) {
        var orList = new ArrayList<PreparedRestrictionPredicate>();
        for (var item : ingredient.getItems()) {
            Objects.requireNonNull(item);
            orList.add(resolveItemStackPredicate(item, ingredient));
        }
        return Or.INSTANCE.prepare(orList);
    }
//
//    protected void indexResult(RecipeHolder recipeHolder) {
//        var itemStack = recipeHolder.recipe.getResultItem(lookup);
//        var item = itemStack.getItem();
//        recipesForOutput.computeIfAbsent(item, unused -> new HashSet<>()).add(recipeHolder);
//    }
//
//    protected void indexRelated(Item item, RecipeHolder recipeHolder) {
//
//    }
//
//    protected void indexIngredient(Ingredient ingredient, RecipeHolder recipeHolder) {
//        var itemStacks = ingredient.getItems();
//        for (var itemStack : itemStacks) {
//            var item = itemStack.getItem();
//            indexItem(item, recipeHolder);
//        }
//    }
//
//    protected void indexItem(Item item, RecipeHolder recipeHolder) {
//        recipesForInput.computeIfAbsent(item, unused -> new HashSet<>()).add(recipeHolder);
//    }

    protected record Entry(PreparedRestrictionPredicate implicitPredicate) {
    }

    public record RecipeHolder(ResourceLocation id, Recipe<?> recipe, int hash) {
        @Override
        public int hashCode() {
            return hash;
        }
    }
}
