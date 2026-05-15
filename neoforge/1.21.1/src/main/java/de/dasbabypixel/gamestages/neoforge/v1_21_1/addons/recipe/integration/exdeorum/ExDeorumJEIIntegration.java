package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve.BuildableSieveRecipe;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve.JEISieveRecipe;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve.RecipeManagerPlugin;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve.SieveCategory;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei.JEIAddon;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.library.ingredients.TypedIngredient;
import org.jspecify.annotations.NullMarked;
import thedarkcolour.exdeorum.compat.CompatUtil;
import thedarkcolour.exdeorum.compat.XeiSieveRecipe;
import thedarkcolour.exdeorum.compat.jei.ExDeorumJeiPlugin;
import thedarkcolour.exdeorum.registry.ERecipeTypes;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

@SuppressWarnings("unchecked")
@NullMarked
public class ExDeorumJEIIntegration {
    public static final RecipeType<XeiSieveRecipe> SIEVE;
    public static final RecipeType<XeiSieveRecipe> COMPRESSED_SIEVE;

    static {
        var lookup = MethodHandles.lookup();
        try {
            var jeiLookup = MethodHandles.privateLookupIn(ExDeorumJeiPlugin.class, lookup);

            SIEVE = (RecipeType<XeiSieveRecipe>) Objects.requireNonNull(jeiLookup.findStaticVarHandle(ExDeorumJeiPlugin.class, "SIEVE", RecipeType.class))
                    .get();
            COMPRESSED_SIEVE = (RecipeType<XeiSieveRecipe>) Objects.requireNonNull(jeiLookup.findStaticVarHandle(ExDeorumJeiPlugin.class, "COMPRESSED_SIEVE", RecipeType.class))
                    .get();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {
        JEIAddon.REGISTER_CATEGORIES_EVENT.addListener(ExDeorumJEIIntegration::registerCategories);
        JEIAddon.REGISTER_ADVANCED_EVENT.addListener(ExDeorumJEIIntegration::registerAdvanced);
    }

    private static void registerCategories(JEIAddon.RegisterCategoriesEvent event) {
        var registration = event.registration();
        registration.addRecipeCategories(new SieveCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    private static void registerAdvanced(JEIAddon.RegisterAdvancedEvent event) {
        var registration = event.registration();
        var helpers = event.registration().getJeiHelpers();
        var ingredientManager = helpers.getIngredientManager();
        var prepared = BuildableSieveRecipe.prepareAll(ingredientManager, Objects.requireNonNull(ERecipeTypes.SIEVE)
                .get(), JEISieveRecipe.SIEVE_ROWS);
        var sieves = CompatUtil.getAvailableSieves(true, true);
        var recipeManagerPlugin = new RecipeManagerPlugin(ingredientManager);
        for (var sieveRecipe : prepared.sieveRecipes()) {
            recipeManagerPlugin.addRecipe(JEISieveRecipe.RECIPE_TYPE, sieveRecipe);
        }
        for (var sieve : sieves) {
            var stack = Objects.requireNonNull(sieve).asItem().getDefaultInstance();
            var ingredient = Objects.requireNonNull(TypedIngredient.createAndFilterInvalid(ingredientManager, VanillaTypes.ITEM_STACK, stack, true));
            recipeManagerPlugin.registerCatalyst(ingredient, JEISieveRecipe.RECIPE_TYPE, prepared.sieveRecipes());
        }
        registration.addRecipeManagerPlugin(recipeManagerPlugin);
    }
}
