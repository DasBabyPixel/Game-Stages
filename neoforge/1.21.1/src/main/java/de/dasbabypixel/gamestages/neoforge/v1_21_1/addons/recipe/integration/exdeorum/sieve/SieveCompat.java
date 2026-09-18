package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionEntry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.ExDeorumJEIIntegration;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei.RecipeConverter;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei.RecipeVisibilityUpdater;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei.JEIAddon;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.ingredients.TypedIngredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import thedarkcolour.exdeorum.compat.CompatUtil;
import thedarkcolour.exdeorum.compat.jei.ExDeorumJeiPlugin;
import thedarkcolour.exdeorum.data.TranslationKeys;
import thedarkcolour.exdeorum.material.DefaultMaterials;
import thedarkcolour.exdeorum.recipe.sieve.SieveRecipe;
import thedarkcolour.exdeorum.registry.ERecipeTypes;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@NullMarked
public class SieveCompat {
    private static final List<SieveRegistration<?>> REGISTRATIONS;

    static {
        var lookup = MethodHandles.lookup();
        try {
            var jeiLookup = MethodHandles.privateLookupIn(ExDeorumJeiPlugin.class, lookup);

            var registrations = new ArrayList<SieveRegistration<?>>();
            registrations.add(new SieveRegistration<>(ERecipeTypes.SIEVE, lookup(jeiLookup.findStaticVarHandle(ExDeorumJeiPlugin.class, "SIEVE", RecipeType.class)), "sieve", DefaultMaterials.OAK_SIEVE, TranslationKeys.SIEVE_CATEGORY_TITLE, () -> CompatUtil.getAvailableSieves(true, true)));
            registrations.add(new SieveRegistration<>(ERecipeTypes.COMPRESSED_SIEVE, lookup(jeiLookup.findStaticVarHandle(ExDeorumJeiPlugin.class, "COMPRESSED_SIEVE", RecipeType.class)), "compressed_sieve", DefaultMaterials.OAK_COMPRESSED_SIEVE, TranslationKeys.COMPRESSED_SIEVE_CATEGORY_TITLE, () -> CompatUtil.getAvailableCompressedSieves(true)));
            REGISTRATIONS = List.copyOf(registrations);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> RecipeType<T> lookup(@Nullable VarHandle handle) {
        return (RecipeType<T>) Objects.requireNonNull(Objects.requireNonNull(handle).get());
    }

    public static void init() {
        JEIAddon.REGISTER_CATEGORIES_EVENT.addListener(SieveCompat::registerCategories);
        JEIAddon.REGISTER_RECIPE_CATALYSTS_EVENT.addListener(SieveCompat::registerCatalysts);
        JEIAddon.RUNTIME_AVAILABLE_EVENT.addListener(SieveCompat::runtimeAvailable);
        JEIAddon.REGISTER_ADVANCED_EVENT.addListener(SieveCompat::registerAdvanced);
        Addon.CLIENT_REPLACE_MANAGER_EVENT.addListener(SieveCompat::handleReplace);

        RecipeVisibilityUpdater.REGISTER_CONVERTERS_EVENT.addListener(SieveCompat::registerConverters);
    }

    private static void registerConverters(RecipeVisibilityUpdater.RegisterConverters event) {
        for (var registration : REGISTRATIONS) {
            register(event, registration);
        }
    }

    private static <T extends SieveRecipe> void register(RecipeVisibilityUpdater.RegisterConverters event, SieveRegistration<T> registration) {
        event.register(registration.mcType.get(), converter(registration));
    }

    private static <T extends SieveRecipe> RecipeConverter<T, ?, ?> converter(SieveRegistration<T> registration) {
        return new SieveConverter<>(registration);
    }

    private static void runtimeAvailable(JEIAddon.RuntimeAvailableEvent event) {
        if (disabled()) return;
        var recipeManager = event.runtime().getRecipeManager();
        for (var registration : REGISTRATIONS) {
            recipeManager.hideRecipeCategory(registration.nativeExDeorumType);
        }
    }

//    private static <T extends SieveRecipe> InvalidationEntry<T> collectInvalidated(RecipeManager recipeManager, ClientPlayerStages stages, SieveRegistration<T> registration) {
//        var invalidatedRecipes = new ArrayList<RecipeHolder<T>>();
//        var mcSieveRecipeType = registration.mcType.get();
//        var recipeHolders = recipeManager.getAllRecipesFor(mcSieveRecipeType);
//        var compiledEntries = new HashSet<CommonRecipeRestrictionEntry.Compiled>();
//        for (var recipeHolder : recipeHolders) {
//            Objects.requireNonNull(recipeHolder);
//            var entry = VRecipeAddon.getEntry(stages, recipeHolder);
//            if (entry == null) continue;
//            invalidatedRecipes.add(recipeHolder);
//            compiledEntries.add(entry);
//        }
//
//        return new InvalidationEntry<>(registration, invalidatedRecipes, compiledEntries);
//    }
//
//    private static void invalidateSieveRecipes(Collection<InvalidationEntry<?>> invalidationEntries) {
//        var stages = ClientGameStageManager.stages();
//        var invalidatedJei = new HashMap<SieveRegistration<?>, Set<BuildableSieveRecipe<?>>>();
//        for (var entry : invalidatedRecipes.entrySet()) {
//            Objects.requireNonNull(entry);
//            var set = new HashSet<BuildableSieveRecipe<?>>();
//            for (var invalidatedRecipe : entry.getValue()) {
//                var jei = jeiRecipeByHolder.get(invalidatedRecipe);
//                if (jei == null) continue;
//                set.add(jei);
//            }
//            if (!set.isEmpty()) invalidatedJei.put(entry.getKey(), set);
//        }
//        if (invalidatedJei.isEmpty()) return;
//        for (var entry : invalidatedJei.entrySet()) {
//            Objects.requireNonNull(entry);
//            var registration = entry.getKey();
//            var visibilityMap = registration.sieveRowVisibility;
//            System.out.println("invalidating " + entry.getValue().size());
//
//            for (var recipe : entry.getValue()) {
//                recipe.updatable().clearCache();
//                var visibleCount = computeVisibleCount(stages, recipe);
//                var rowCount = requiredRowCount(visibleCount);
//                visibilityMap.update(recipe, rowCount);
//            }
//            visibilityMap.updateMaxRowCount();
//        }
//    }
//
//    private static int requiredRowCount(int visibleCount) {
//        return (visibleCount + 8) / 9;
//    }
//
//    private static int computeVisibleCount(BaseStages stages, BuildableSieveRecipe<?> recipe) {
//        int visibleCount = 0;
//        for (var possibleResult : recipe.possibleResults()) {
//            var compiled = VRecipeAddon.getEntry(stages, possibleResult.holder());
//            var visible = compiled == null || compiled.predicate().test();
//            if (visible) visibleCount++;
//        }
//        return visibleCount;
//    }

    private static void registerCatalysts(JEIAddon.RegisterRecipeCatalystsEvent event) {
        if (disabled()) return;
        var registration = event.registration();
        for (var r : REGISTRATIONS) {
            registerCatalysts(registration, r);
        }
    }

    private static <T extends SieveRecipe> void registerCatalysts(IRecipeCatalystRegistration registration, SieveRegistration<T> r) {
        registration.addRecipeCatalysts(r.customType, r.sieves.get());
    }

    private static void registerAdvanced(JEIAddon.RegisterAdvancedEvent event) {
        if (disabled()) return;

        var registration = event.registration();
        var helpers = event.registration().getJeiHelpers();
        var ingredientManager = helpers.getIngredientManager();
        var recipeManagerPlugin = new RecipeManagerPlugin(ingredientManager);
        for (var r : REGISTRATIONS) {
            registerAdvanced(recipeManagerPlugin, ingredientManager, r);
        }

        registration.addRecipeManagerPlugin(recipeManagerPlugin);
    }

    private static <T extends SieveRecipe> void registerAdvanced(RecipeManagerPlugin recipeManagerPlugin, IIngredientManager ingredientManager, SieveRegistration<T> r) {
        var jeiType = r.customType;
        var prepared = Objects.requireNonNull(r.preparedCache);

        for (var sieveRecipe : prepared.sieveRecipes()) {
            recipeManagerPlugin.addRecipe(jeiType, sieveRecipe.updatable(), sieveRecipe);
        }
        for (var sieve : r.sieves.get()) {
            var stack = Objects.requireNonNull(sieve).asItem().getDefaultInstance();
            var ingredient = Objects.requireNonNull(TypedIngredient.createAndFilterInvalid(ingredientManager, VanillaTypes.ITEM_STACK, stack, true));
//            var updatable = prepared.sieveRecipes().stream().map(BuildableSieveRecipe::updatable).toList();
            recipeManagerPlugin.registerCategoryCatalyst(ingredient, jeiType);
//            recipeManagerPlugin.registerCatalyst(ingredient, jeiType, updatable);
        }
    }

    private static void handleReplace(Addon.ClientReplaceManagerEvent event) {
        var manager = event.newManager();
        if (manager == null) {
            for (var registration : REGISTRATIONS) {
                registration.reset();
            }
        }
    }

    private static void registerCategories(JEIAddon.RegisterCategoriesEvent event) {
        if (disabled()) return;
        var registration = event.registration();

        for (var r : REGISTRATIONS) {
            registerCategories(event, r);
        }

        var helper = registration.getJeiHelpers().getGuiHelper();
        var c = REGISTRATIONS
                .stream()
                .map(r -> SieveCategory.sieve(helper, r.icon, r.translationKey, r.rows, r.customType))
                .toArray(IRecipeCategory[]::new);
        registration.addRecipeCategories(c);
    }

    private static <T extends SieveRecipe> void registerCategories(JEIAddon.RegisterCategoriesEvent event, SieveRegistration<T> registration) {
        var ingredientManager = event.registration().getJeiHelpers().getIngredientManager();
        registration.preparedCache = BuildableSieveRecipe.prepareAll(ingredientManager, registration.mcType.get());
    }

    private static boolean disabled() {
        return ExDeorumJEIIntegration.disabled();
    }

    private record InvalidationEntry<T extends SieveRecipe>(SieveRegistration<T> registration,
                                                            List<RecipeHolder<T>> invalidatedRecipes,
                                                            Set<CommonRecipeRestrictionEntry.Compiled> compiledEntries) {
        private InvalidationEntry {
            invalidatedRecipes = List.copyOf(invalidatedRecipes);
            compiledEntries = Set.copyOf(compiledEntries);
        }
    }

}
