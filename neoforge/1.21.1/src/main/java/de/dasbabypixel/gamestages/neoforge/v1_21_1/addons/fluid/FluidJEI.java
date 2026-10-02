package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class FluidJEI {
    private static @Nullable FluidJEI instance;
    private final FluidVisibilityUpdater updater = new FluidVisibilityUpdater();

    public static void init() {
        if (instance != null) throw new IllegalStateException();
        instance = new FluidJEI();
        instance.updater.markReady(new FluidVisibilityUpdater.FluidContext());
    }
}
