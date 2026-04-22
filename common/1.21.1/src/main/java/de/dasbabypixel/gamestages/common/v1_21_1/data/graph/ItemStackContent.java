package de.dasbabypixel.gamestages.common.v1_21_1.data.graph;

import de.dasbabypixel.gamestages.common.data.graph.DependencyContent;
import de.dasbabypixel.gamestages.common.data.graph.DependencyContentType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record ItemStackContent(ItemStack itemStack) implements DependencyContent {
    public static final Type TYPE = new Type();

    @Override
    public boolean satisfiedBy(DependencyContent content) {
        return equals(content);
    }

    @Override
    public DependencyContentType type() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "ItemStack[" + itemStack + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStack.getItem(), itemStack.getComponentsPatch(), itemStack.getCount());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ItemStackContent(
                ItemStack stack
        ) && ItemStack.isSameItemSameComponents(itemStack, stack) && stack.getCount() == itemStack.getCount();
    }

    public record Type() implements DependencyContentType {
    }
}
