package de.dasbabypixel.gamestages.common.v1_21_1.data.graph;

import de.dasbabypixel.gamestages.common.data.graph.DependencyContent;
import de.dasbabypixel.gamestages.common.data.graph.DependencyContentType;
import net.minecraft.world.item.crafting.Recipe;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record RecipeContent(Recipe<?> recipe) implements DependencyContent {
    public static final Type TYPE = new Type();

    @Override
    public boolean satisfiedBy(DependencyContent content) {
        return equals(content);
    }

    @Override
    public String toString() {
        return "RecipeContent[" + recipe + "]";
    }

    @Override
    public DependencyContentType type() {
        return TYPE;
    }

    public record Type() implements DependencyContentType {
    }
}
