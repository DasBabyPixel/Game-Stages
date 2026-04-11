package de.dasbabypixel.gamestages.common.addon;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface MessageListener<A extends Addon> {
    void handle(A addon, @Nullable Object message);
}
