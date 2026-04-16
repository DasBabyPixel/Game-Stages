package de.dasbabypixel.gamestages.common.v1_21_1.data.graph;

import de.dasbabypixel.gamestages.common.data.graph.DependencyContent;
import de.dasbabypixel.gamestages.common.data.graph.DependencyContentType;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record ItemContent(Item item) implements DependencyContent {
    public static final Type TYPE = new Type();

    @Override
    public boolean satisfiedBy(DependencyContent content) {
        return equals(content) || content instanceof ItemStackContent(
                var itemStack
        ) && item.equals(itemStack.getItem());
    }

    @Override
    public DependencyContentType type() {
        return TYPE;
    }

    public record Type() implements DependencyContentType {
    }
}
