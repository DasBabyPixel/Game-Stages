package de.dasbabypixel.gamestages.neoforge.integration;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record Mod(String id, ModProvider modProvider) {
    public boolean isLoaded() {
        return modProvider.isLoaded(this);
    }
}
