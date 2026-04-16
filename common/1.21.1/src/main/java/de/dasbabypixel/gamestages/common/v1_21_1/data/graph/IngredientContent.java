package de.dasbabypixel.gamestages.common.v1_21_1.data.graph;

import de.dasbabypixel.gamestages.common.data.graph.DependencyContent;
import de.dasbabypixel.gamestages.common.data.graph.DependencyContentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record IngredientContent(Ingredient ingredient) implements DependencyContent {
    public static final Type TYPE = new Type();

    @Override
    public boolean satisfiedBy(DependencyContent content) {
        return equals(content) || content instanceof ItemStackContent(
                ItemStack itemStack
        ) && ingredient.test(itemStack);
    }

    @Override
    public DependencyContentType type() {
        return TYPE;
    }

    public record Type() implements DependencyContentType {
    }
}