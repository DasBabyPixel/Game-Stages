package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.exdeorum;

import net.minecraft.world.item.crafting.RecipeHolder;
import thedarkcolour.exdeorum.recipe.sieve.SieveRecipe;

public interface IXeiSieveRecipeResult {
    void initHolder(RecipeHolder<? extends SieveRecipe> holder);

    RecipeHolder<? extends SieveRecipe> holder();
}
