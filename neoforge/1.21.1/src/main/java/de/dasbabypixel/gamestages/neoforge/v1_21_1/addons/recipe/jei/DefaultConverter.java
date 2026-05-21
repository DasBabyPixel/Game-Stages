package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
@NullMarked
public class DefaultConverter implements RecipeConverter<Recipe<RecipeInput>, RecipeInput, DefaultConverter.Cache> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConverter.class);

    @Override
    public void convert(Context context, List<RecipeAndType<?>> list, RecipeType<Recipe<RecipeInput>> minecraftType, List<RecipeHolder<Recipe<RecipeInput>>> recipeHolders, Cache cache) {
        var jeiType = context.getByMinecraft(minecraftType);
        if (jeiType == null) {
            LOGGER.error("Skipping unknown type {}", BuiltInRegistries.RECIPE_TYPE.getKey(minecraftType));
            return;
        }
        var recipeClass = jeiType.getRecipeClass();
        var recipeList = new ArrayList<>();
        for (var recipeHolder : recipeHolders) {
            if (recipeClass.isInstance(recipeHolder)) {
                recipeList.add(recipeHolder);
            } else if (recipeClass.isInstance(recipeHolder.value())) {
                recipeList.add(recipeHolder.value());
            } else if (recipeClass.isInstance(recipeHolder.id())) {
                recipeList.add(recipeHolder.id());
            } else {
                recipeList.clear();
                LOGGER.error("Failed to convert recipe holder to instance of {}, skipping recipe", recipeClass.getName());
                break;
            }
        }
        for (var o : recipeList) {
            Objects.requireNonNull(o);
            var recipeAndType = new RecipeAndType<>((mezz.jei.api.recipe.RecipeType<Object>) jeiType, o);
            list.add(recipeAndType);
        }
    }

    @Override
    public void invalidate(List<RecipeAndType<?>> list) {
    }

    @Override
    public Cache buildCache(Context context, RecipeType<Recipe<RecipeInput>> minecraftType) {
        return new Cache();
    }

    public static class Cache {

    }
}
