package de.dasbabypixel.gamestages.neoforge.integration;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ModProvider {
    boolean isLoaded(Mod mod);
}
