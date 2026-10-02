package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.exdeorum;

import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NullMarked;
import thedarkcolour.exdeorum.recipe.sieve.SieveRecipe;

@NullMarked
public interface IXeiSieveRecipeResult {
    void initHolder(RecipeHolder<? extends SieveRecipe> holder);

    RecipeHolder<? extends SieveRecipe> holder();
}
