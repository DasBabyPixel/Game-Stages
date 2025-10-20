package de.dasbabypixel.gamestages.neoforge.integration;

public record Mod(String id, ModProvider modProvider) {
    public boolean isLoaded() {
        return modProvider.isLoaded(this);
    }
}
