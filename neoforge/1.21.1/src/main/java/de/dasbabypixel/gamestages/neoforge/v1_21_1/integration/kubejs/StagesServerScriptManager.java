package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface StagesServerScriptManager {
    JSContext context();

    void context(@Nullable JSContext context);
}
