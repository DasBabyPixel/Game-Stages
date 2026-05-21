package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei;

import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

@NullMarked
public record Context(IJeiRuntime runtime,
                      Map<RecipeType<?>, net.minecraft.world.item.crafting.RecipeType<?>> recipeTypeByJEI,
                      Map<net.minecraft.world.item.crafting.RecipeType<?>, RecipeType<?>> recipeTypeByMinecraft) {
    public Context {
        recipeTypeByJEI = Objects.requireNonNull(Map.copyOf(recipeTypeByJEI));
        recipeTypeByMinecraft = Objects.requireNonNull(Map.copyOf(recipeTypeByMinecraft));
    }

    public net.minecraft.world.item.crafting.@Nullable RecipeType<?> getByJEI(RecipeType<?> recipeType) {
        return recipeTypeByJEI.get(recipeType);
    }

    public @Nullable RecipeType<?> getByMinecraft(net.minecraft.world.item.crafting.RecipeType<?> recipeType) {
        return recipeTypeByMinecraft.get(recipeType);
    }
}

