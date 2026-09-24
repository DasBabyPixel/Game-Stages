package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record ModIdJSImpl(String modId) implements ModIdJS {
    @Override
    public String toString() {
        return modId;
    }
}
