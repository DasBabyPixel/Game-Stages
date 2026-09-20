package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@NullMarked
public interface GameContentProvider {
    GameContent emptyContent();

    GameContent union(Collection<? extends GameContent> collection);
}
