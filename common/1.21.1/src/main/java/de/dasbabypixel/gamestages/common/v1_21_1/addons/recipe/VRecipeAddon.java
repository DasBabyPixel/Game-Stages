package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.addon.AddonManager;
import de.dasbabypixel.gamestages.common.addon.ContentRegistry;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ServerMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.compiler.ManagerCompilerTask;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.And;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.Or;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.True;
import de.dasbabypixel.gamestages.common.event.EventType;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VContentRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.messages.RecipeMessages;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.messages.ResolveItemStackPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@NullMarked
public abstract class VRecipeAddon implements VAddon {
    private static @Nullable VRecipeAddon instance;

    public VRecipeAddon() {
        instance = this;
        REGISTER_CUSTOM_CONTENT_EVENT.addListener(this::handle);
        REGISTER_PACKETS_EVENT.addListener(this::handle);
        PRE_COMPILE_PREPARE_EVENT.addListener(this::handle);
        PRE_COMPILE_TYPE_EVENT.addListener(EventType.ORDER_MONITOR - 10, this::handle);
    }

    private void handle(RegisterCustomContentEvent event) {
        event.contentRegistry()
                .prepare(CommonRecipeCollection.TYPE)
                .set(ContentRegistry.NAME, "recipe")
                .set(ContentRegistry.FLATTENER_FACTORY, new RecipeFlattenerFactory())
                .set(VContentRegistry.GAME_CONTENT_SERIALIZER, CommonRecipeCollection.SERIALIZER)
                .register();
    }

    @SuppressWarnings("DataFlowIssue")
    private PreparedRestrictionPredicate resolveItemStackPredicate(ManagerCompilerTask manager, ItemStack itemStack, @Nullable Ingredient ingredient) {
        var msg = AddonManager.instance()
                .sendMessage(VRecipeAddon.instance(), RecipeMessages.ORIGIN_ID, ResolveItemStackPredicate.ID, ResolveItemStackPredicate::new, manager, itemStack, ingredient);
        return msg == null ? True.INSTANCE.prepare() : msg.predicate();
    }

    private void handle(PreCompilePrepareEvent event) {
        event.addEvaluationDependency(CommonRecipeCollection.TYPE, CommonItemCollection.TYPE);
    }

    private void handle(PreCompileTypeEvent event) {
        if (event.type() != CommonRecipeCollection.TYPE) return;
        var task = event.task();

        if (task.manager() instanceof ServerMutableGameStageManager serverManager) {
            var restrictionsByType = task.restrictionsByType(CommonRecipeCollection.TYPE);
            var registryAccess = serverManager.get(REGISTRY_ATTRIBUTE);
            var recipeRestrictions = new ArrayList<CommonRecipeRestrictionEntry>();
            for (var restrictionEntry : restrictionsByType) {
                recipeRestrictions.add((CommonRecipeRestrictionEntry) restrictionEntry);
            }
            restrictionsByType.clear();
            var implicitPredicateByRecipe = new HashMap<ResourceLocation, PreparedRestrictionPredicate>();

            var recipeManager = serverManager.get(SERVER_RESOURCES_ATTRIBUTE).getRecipeManager();
            for (var recipeHolder : recipeManager.getRecipes()) {
                Objects.requireNonNull(recipeHolder);
                var recipe = recipeHolder.value();
                var ingredientPredicates = new ArrayList<PreparedRestrictionPredicate>();
                for (var ingredient : recipe.getIngredients()) {
                    Objects.requireNonNull(ingredient);
                    if (ingredient.isEmpty()) continue;
                    var itemPredicates = new ArrayList<PreparedRestrictionPredicate>();
                    for (var item : ingredient.getItems()) {
                        Objects.requireNonNull(item);
                        itemPredicates.add(resolveItemStackPredicate(task, item, ingredient));
                    }
                    if (!itemPredicates.isEmpty()) {
                        ingredientPredicates.add(Or.INSTANCE.prepare(itemPredicates));
                    }
                }
                ingredientPredicates.add(resolveItemStackPredicate(task, recipe.getResultItem(registryAccess), null));
                var ingredientPredicate = And.INSTANCE.prepare(ingredientPredicates);

                implicitPredicateByRecipe.put(recipeHolder.id(), ingredientPredicate);
            }

            var finalRecipesByRestrictionAndPredicate = new HashMap<CommonRecipeRestrictionEntry, HashMap<PreparedRestrictionPredicate, ArrayList<ResourceLocation>>>();
            var unrestrictedRecipes = new HashSet<>(implicitPredicateByRecipe.keySet());
            for (var restriction : recipeRestrictions) {
                var restrictionContent = restriction.gameContent();

                for (var recipe : ((CommonRecipeCollection) restrictionContent).recipes()) {
                    unrestrictedRecipes.remove(recipe);
                    var implicitPredicate = Objects.requireNonNull(implicitPredicateByRecipe.get(recipe));
                    var predicate = restriction.predicate().and(implicitPredicate);
                    var byRestriction = finalRecipesByRestrictionAndPredicate.computeIfAbsent(restriction, ignored -> new HashMap<>());
                    byRestriction.computeIfAbsent(predicate, ignored -> new ArrayList<>()).add(recipe);
                }
            }
            if (!unrestrictedRecipes.isEmpty()) {
                var byPredicate = new HashMap<PreparedRestrictionPredicate, List<ResourceLocation>>();
                for (var unrestrictedRecipe : unrestrictedRecipes) {
                    var predicate = Objects.requireNonNull(implicitPredicateByRecipe.get(unrestrictedRecipe));
                    byPredicate.computeIfAbsent(predicate, ignored -> new ArrayList<>()).add(unrestrictedRecipe);
                }

                for (var entry : byPredicate.entrySet()) {
                    Objects.requireNonNull(entry);
                    var predicate = entry.getKey();
                    var recipes = Objects.requireNonNull(entry.getValue());
                    var restriction = createDefaultEntry(predicate, new CommonRecipeCollection(recipes));
                    restrictionsByType.add(restriction);
                }
            }

            for (var entry : finalRecipesByRestrictionAndPredicate.entrySet()) {
                Objects.requireNonNull(entry);
                var restriction = entry.getKey();
                var recipesByRestriction = entry.getValue();
                for (var entry2 : recipesByRestriction.entrySet()) {
                    Objects.requireNonNull(entry2);
                    var predicate = entry2.getKey();
                    var recipes = entry2.getValue();
                    var recipesContent = new CommonRecipeCollection(Objects.requireNonNull(List.copyOf(recipes)));

                    var newRestriction = restriction.copyWith(predicate, recipesContent);
                    restrictionsByType.add(newRestriction);
                }
            }
        }
    }

    private void handle(RegisterPacketsEvent event) {
        var registry = event.registry();
        registry.playClientBound(CommonRecipeRestrictionPacket.TYPE, CommonRecipeRestrictionPacket.STREAM_CODEC);
    }

    protected abstract CommonRecipeRestrictionEntry createDefaultEntry(PreparedRestrictionPredicate predicate, CommonRecipeCollection recipes);

    public abstract void handle(CommonRecipeRestrictionPacket packet);

    public static CommonRecipeRestrictionEntry.@Nullable Compiled getEntry(BaseStages stages, RecipeHolder<?> holder) {
        var compileIndex = stages.get(BaseStages.CompileIndex.ATTRIBUTE);
        var typeIndex = compileIndex.typeIndex(CommonRecipeCollection.TYPE);
        var entry = typeIndex.entryByContent().get(holder.id());
        return (CommonRecipeRestrictionEntry.Compiled) entry;
    }

    public static VRecipeAddon instance() {
        return Objects.requireNonNull(instance);
    }
}
