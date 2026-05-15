package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface IIngredientSupplier {
    List<ITypedIngredient<?>> getIngredients(RecipeIngredientRole role);
}
