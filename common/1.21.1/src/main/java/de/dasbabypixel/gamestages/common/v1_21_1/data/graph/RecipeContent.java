package de.dasbabypixel.gamestages.common.v1_21_1.data.graph;

import de.dasbabypixel.gamestages.common.data.graph.DependencyContent;
import de.dasbabypixel.gamestages.common.data.graph.DependencyContentType;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record RecipeContent(RecipeHolder<?> recipe) implements DependencyContent {
    public static final Type TYPE = new Type();

    @Override
    public boolean satisfiedBy(DependencyContent content) {
        return equals(content);
    }

    @Override
    public String toString() {
        return "Recipe[" + recipe.id() + "]";
    }

    @Override
    public DependencyContentType type() {
        return TYPE;
    }

    @Override
    public int hashCode() {
        return recipe.id().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RecipeContent(var r) && r.id().equals(recipe.id());
    }

    public record Type() implements DependencyContentType {
    }
}
