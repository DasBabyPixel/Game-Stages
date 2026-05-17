package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeJEI;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.ExDeorumJEIIntegration;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei.JEIAddon;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.Internal;
import mezz.jei.library.ingredients.TypedIngredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.mutable.MutableInt;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

@NullMarked
public class SieveCompat {
    private static final List<SieveRegistration> REGISTRATIONS;
    private static final Map<RecipeHolder<?>, BuildableSieveRecipe> jeiRecipeByHolder = new HashMap<>();

    static {
        var lookup = MethodHandles.lookup();
        try {
            var jeiLookup = MethodHandles.privateLookupIn(ExDeorumJeiPlugin.class, lookup);

            var registrations = new ArrayList<SieveRegistration>();
            registrations.add(new SieveRegistration(ERecipeTypes.SIEVE, lookup(jeiLookup.findStaticVarHandle(ExDeorumJeiPlugin.class, "SIEVE", RecipeType.class)), "sieve", DefaultMaterials.OAK_SIEVE, TranslationKeys.SIEVE_CATEGORY_TITLE, () -> CompatUtil.getAvailableSieves(true, true)));
            registrations.add(new SieveRegistration(ERecipeTypes.COMPRESSED_SIEVE, lookup(jeiLookup.findStaticVarHandle(ExDeorumJeiPlugin.class, "COMPRESSED_SIEVE", RecipeType.class)), "compressed_sieve", DefaultMaterials.OAK_COMPRESSED_SIEVE, TranslationKeys.COMPRESSED_SIEVE_CATEGORY_TITLE, () -> CompatUtil.getAvailableCompressedSieves(true)));
            REGISTRATIONS = List.copyOf(registrations);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static class SieveRegistration {
        private final DeferredHolder<net.minecraft.world.item.crafting.RecipeType<?>, ? extends net.minecraft.world.item.crafting.RecipeType<? extends SieveRecipe>> mcType;
        private final RecipeType<?> nativeExDeorumType;
        private final RecipeType<JEISieveRecipe> customType;
        private final ItemLike icon;
        private final String translationKey;
        private final MutableInt rows = new MutableInt();
        private final SieveRowVisibility sieveRowVisibility = new SieveRowVisibility(rows);
        private final Supplier<ItemLike[]> sieves;

        public <T extends SieveRecipe> SieveRegistration(@Nullable DeferredHolder<net.minecraft.world.item.crafting.RecipeType<?>, net.minecraft.world.item.crafting.RecipeType<T>> mcType, RecipeType<?> nativeExDeorumType, String customType, ItemLike icon, String translationKey, Supplier<List<ItemLike>> sieves) {
            this.mcType = Objects.requireNonNull(mcType);
            this.nativeExDeorumType = nativeExDeorumType;
            this.customType = RecipeType.create("exdeorum", "stages_" + customType, JEISieveRecipe.class);
            this.icon = icon;
            this.translationKey = translationKey;
            this.sieves = () -> sieves.get().toArray(ItemLike[]::new);
        }

        public void reset() {
            rows.setValue(0);
            sieveRowVisibility.reset();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> RecipeType<T> lookup(@Nullable VarHandle handle) {
        return (RecipeType<T>) Objects.requireNonNull(Objects.requireNonNull(handle).get());
    }

    public static void init() {
        JEIAddon.REGISTER_CATEGORIES_EVENT.addListener(SieveCompat::registerCategories);
        JEIAddon.REGISTER_ADVANCED_EVENT.addListener(SieveCompat::registerAdvanced);
        JEIAddon.REGISTER_RECIPE_CATALYSTS_EVENT.addListener(SieveCompat::registerCatalysts);
        JEIAddon.RUNTIME_AVAILABLE_EVENT.addListener(SieveCompat::runtimeAvailable);
        Addon.CLIENT_REPLACE_MANAGER_EVENT.addListener(SieveCompat::handleReplace);
        Addon.CLIENT_RECOMPILE_POST_EVENT.addListener(SieveCompat::postCompile);
    }

    private static void runtimeAvailable(JEIAddon.RuntimeAvailableEvent event) {
        if (disabled()) return;
        var recipeManager = event.runtime().getRecipeManager();
        for (var registration : REGISTRATIONS) {
            recipeManager.hideRecipeCategory(registration.nativeExDeorumType);
        }
    }

    private static void postCompile(Addon.ClientRecompilePostEvent event) {
        if (disabled()) return;
        var recipeManager = RecipeJEI.recipeManager();
        var stages = event.stages();
        var sieveEntries = new HashMap<CommonRecipeRestrictionEntry.Compiled, Map<SieveRegistration, List<RecipeHolder<? extends SieveRecipe>>>>();
        for (var registration : REGISTRATIONS) {
            var mcSieveRecipeType = registration.mcType.get();
            var recipeHolders = recipeManager.getAllRecipesFor(mcSieveRecipeType);
            for (var recipeHolder : recipeHolders) {
                Objects.requireNonNull(recipeHolder);
                var entry = VRecipeAddon.getEntry(stages, recipeHolder);
                if (entry == null) continue;
                sieveEntries
                        .computeIfAbsent(entry, i -> new HashMap<>())
                        .computeIfAbsent(registration, i -> new ArrayList<>())
                        .add(recipeHolder);
            }
        }

        for (var entry_ : sieveEntries.entrySet()) {
            Objects.requireNonNull(entry_);
            var entry = entry_.getKey();
            var recipeHolders = entry_.getValue();

            entry.predicate().addNotifier(newTest -> invalidateSieveRecipes(recipeHolders));
            invalidateSieveRecipes(recipeHolders);
        }
    }

    private static int requiredRowCount(int visibleCount) {
        return (visibleCount + 8) / 9;
    }

    private static int computeVisibleCount(BaseStages stages, BuildableSieveRecipe recipe) {
        int visibleCount = 0;
        for (var possibleResult : recipe.possibleResults()) {
            var compiled = VRecipeAddon.getEntry(stages, possibleResult.holder());
            var visible = compiled == null || compiled.predicate().test();
            if (visible) visibleCount++;
        }
        return visibleCount;
    }

    private static void registerCatalysts(JEIAddon.RegisterRecipeCatalystsEvent event) {
        if (disabled()) return;
        var registration = event.registration();
        for (var r : REGISTRATIONS) {
            registration.addRecipeCatalysts(r.customType, r.sieves.get());
        }
    }

    private static void registerAdvanced(JEIAddon.RegisterAdvancedEvent event) {
        jeiRecipeByHolder.clear();
        if (disabled()) return;

        var registration = event.registration();
        var helpers = event.registration().getJeiHelpers();
        var ingredientManager = helpers.getIngredientManager();
        var recipeManagerPlugin = new RecipeManagerPlugin(ingredientManager);
        for (var r : REGISTRATIONS) {
            var mcType = r.mcType.get();
            var jeiType = r.customType;
            var prepared = BuildableSieveRecipe.prepareAll(ingredientManager, mcType);

            for (var sieveRecipe : prepared.sieveRecipes()) {
                for (var possibleResult : sieveRecipe.possibleResults()) {
                    jeiRecipeByHolder.put(possibleResult.holder(), sieveRecipe);
                }
            }

            for (var sieveRecipe : prepared.sieveRecipes()) {
                recipeManagerPlugin.addRecipe(jeiType, sieveRecipe);
            }
            for (var sieve : r.sieves.get()) {
                var stack = Objects.requireNonNull(sieve).asItem().getDefaultInstance();
                var ingredient = Objects.requireNonNull(TypedIngredient.createAndFilterInvalid(ingredientManager, VanillaTypes.ITEM_STACK, stack, true));
                recipeManagerPlugin.registerCatalyst(ingredient, jeiType, prepared.sieveRecipes());
            }
        }

        registration.addRecipeManagerPlugin(recipeManagerPlugin);
    }

    private static void invalidateSieveRecipes(Map<SieveRegistration, List<RecipeHolder<? extends SieveRecipe>>> invalidatedRecipes) {
        var stages = ClientGameStageManager.stages();
        var invalidatedJei = new HashMap<SieveRegistration, Set<BuildableSieveRecipe>>();
        for (var entry : invalidatedRecipes.entrySet()) {
            Objects.requireNonNull(entry);
            var set = new HashSet<BuildableSieveRecipe>();
            for (var invalidatedRecipe : entry.getValue()) {
                var jei = jeiRecipeByHolder.get(invalidatedRecipe);
                if (jei == null) continue;
                set.add(jei);
            }
            if (!set.isEmpty()) invalidatedJei.put(entry.getKey(), set);
        }
        if (invalidatedJei.isEmpty()) return;
        for (var entry : invalidatedJei.entrySet()) {
            Objects.requireNonNull(entry);
            var registration = entry.getKey();
            var visibilityMap = registration.sieveRowVisibility;

            for (var recipe : entry.getValue()) {
                var visibleCount = computeVisibleCount(stages, recipe);
                var rowCount = requiredRowCount(visibleCount);
                visibilityMap.update(recipe, rowCount);
            }
            visibilityMap.updateMaxRowCount();
        }
    }

    private static void handleReplace(Addon.ClientReplaceManagerEvent event) {
        var manager = event.newManager();
        if (manager == null) {
            jeiRecipeByHolder.clear();
            for (var registration : REGISTRATIONS) {
                registration.reset();
            }
        }
    }

    private static void registerCategories(JEIAddon.RegisterCategoriesEvent event) {
        if (disabled()) return;
        var registration = event.registration();

        var helper = registration.getJeiHelpers().getGuiHelper();
        var c = REGISTRATIONS
                .stream()
                .map(r -> SieveCategory.sieve(helper, r.icon, r.translationKey, r.rows, r.customType))
                .toArray(IRecipeCategory[]::new);
        registration.addRecipeCategories(c);
    }

    private static boolean disabled() {
        return ExDeorumJEIIntegration.disabled();
    }

    private static class SieveRowVisibility {
        private final NavigableMap<Integer, Set<BuildableSieveRecipe>> recipesByRowCount = new TreeMap<>();
        private final Map<BuildableSieveRecipe, Integer> rowCountByRecipe = new HashMap<>();
        private final MutableInt rows;
        private int maxRowCount = 0;

        public SieveRowVisibility(MutableInt rows) {
            this.rows = rows;
        }

        public void reset() {
            recipesByRowCount.clear();
            rowCountByRecipe.clear();
            maxRowCount = 0;
        }

        public void update(BuildableSieveRecipe recipe, int newRowCount) {
            var oldRowCount = rowCountByRecipe.get(recipe);
            if (oldRowCount != null) {
                if (oldRowCount == newRowCount) return;
                recipesByRowCount.compute(oldRowCount, (c, s) -> {
                    Objects.requireNonNull(s).remove(recipe);
                    return s.isEmpty() ? null : s;
                });
                recipesByRowCount.computeIfAbsent(newRowCount, i -> new HashSet<>()).add(recipe);
            } else {
                recipesByRowCount.computeIfAbsent(newRowCount, i -> new HashSet<>()).add(recipe);
            }
            rowCountByRecipe.put(recipe, newRowCount);
        }

        public boolean updateMaxRowCount() {
            var newMaxRowCount = recipesByRowCount.isEmpty() ? 0 : recipesByRowCount.lastKey();
            if (maxRowCount == newMaxRowCount) return false;
            maxRowCount = newMaxRowCount;
            rows.setValue(newMaxRowCount);
            // TODO more update?
            return true;
        }
    }
}
