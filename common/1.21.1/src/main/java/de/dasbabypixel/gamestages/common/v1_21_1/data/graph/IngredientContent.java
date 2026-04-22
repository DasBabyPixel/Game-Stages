package de.dasbabypixel.gamestages.common.v1_21_1.data.graph;

import de.dasbabypixel.gamestages.common.data.graph.DependencyContent;
import de.dasbabypixel.gamestages.common.data.graph.DependencyContentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record IngredientContent(Ingredient ingredient) implements DependencyContent {
    public static final Type TYPE = new Type();
    @SuppressWarnings("NotNullFieldNotInitialized")
    public static PlatformIngredientHelper platformIngredientHelper;

    @Override
    public boolean satisfiedBy(DependencyContent content) {
        return equals(content) || content instanceof ItemStackContent(
                ItemStack itemStack
        ) && ingredient.test(itemStack);
    }

    @Override
    public String toString() {
        return "Ingredient[" + platformIngredientHelper.toString(ingredient) + "]";
    }

    @Override
    public DependencyContentType type() {
        return TYPE;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IngredientContent(var i) && i.equals(ingredient);
    }

    @Override
    public int hashCode() {
        return ingredient.hashCode();
    }

    public interface PlatformIngredientHelper {
        String toString(Ingredient ingredient);
    }

    public record Type() implements DependencyContentType {
    }
}