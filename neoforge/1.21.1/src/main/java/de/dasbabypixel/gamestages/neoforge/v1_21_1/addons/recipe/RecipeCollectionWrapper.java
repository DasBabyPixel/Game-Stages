package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.ContentWrapper;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record RecipeCollectionWrapper(GameContent content) implements ContentWrapper {
}
