package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration;

import mezz.jei.api.recipe.RecipeType;
import org.jspecify.annotations.NullMarked;
import thedarkcolour.exdeorum.compat.XeiSieveRecipe;
import thedarkcolour.exdeorum.compat.jei.ExDeorumJeiPlugin;

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
}
