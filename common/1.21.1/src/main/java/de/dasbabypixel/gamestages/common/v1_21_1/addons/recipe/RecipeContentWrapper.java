package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.GameContentDirect;
import de.dasbabypixel.gamestages.common.data.GameContentWrapper;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record RecipeContentWrapper(
        GameContentDirect<RecipeType.RecipeData, List<ResourceLocation>, ResourceLocation> gameContent) implements GameContentWrapper.Direct {
}
