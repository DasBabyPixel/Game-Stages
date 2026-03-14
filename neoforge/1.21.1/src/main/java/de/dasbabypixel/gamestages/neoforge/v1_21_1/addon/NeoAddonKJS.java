package de.dasbabypixel.gamestages.neoforge.v1_21_1.addon;

import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;

public interface NeoAddonKJS {
    default void registerEventExtensions(EventRegistry registry) {
    }

    default void registerTypeWrappers(TypeWrapperRegistry registry) {
    }
}
