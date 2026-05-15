package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;

import java.util.List;

public interface PossibleOutputs {
    List<ITypedIngredient<?>> possibleOutputs(IIngredientManager ingredientManager);
}
