package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;

import mezz.jei.api.runtime.IJeiRuntime;
import org.jspecify.annotations.NonNull;

public interface JEIListener {
    void onRuntimeAvailable(@NonNull IJeiRuntime runtime);
}
