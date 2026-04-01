package de.dasbabypixel.gamestages.neoforge.v1_21_1.addon;

import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import org.jspecify.annotations.NonNull;

public interface NeoAddonKJS {
    default void registerEventExtensions(@NonNull EventRegistry registry) {
    }

    default void registerTypeWrappers(@NonNull TypeWrapperRegistry registry) {
    }
}
