package de.dasbabypixel.gamestages.common.data.graph;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface DependencyContent {
    /**
     * Tests whether the given content satisfies the input needs of this content.
     * If {@code content.equals(this)}, then this must return true.
     * Can also return true for other content, that is a subset of this content.
     * One example of this could be this is an ingredient, the given content is an ItemStack that satisfies this.
     */
    boolean satisfiedBy(DependencyContent content);

    @Override
    String toString();

    DependencyContentType type();
}
