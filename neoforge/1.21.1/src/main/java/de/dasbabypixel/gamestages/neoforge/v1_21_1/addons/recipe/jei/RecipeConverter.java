package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface RecipeConverter<T extends Recipe<I>, I extends RecipeInput, Cache> {
    void convert(Context context, List<RecipeAndType<?>> list, net.minecraft.world.item.crafting.RecipeType<T> minecraftType, List<RecipeHolder<T>> recipeHolders, Cache cache);

    void invalidate(List<RecipeAndType<?>> list);

    Cache buildCache(Context context, net.minecraft.world.item.crafting.RecipeType<T> minecraftType);
}
