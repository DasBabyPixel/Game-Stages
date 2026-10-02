package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jei;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ItemJEI {
    private static @Nullable ItemJEI instance;
    private final ItemVisibilityUpdater updater = new ItemVisibilityUpdater();

    private ItemJEI() {
    }

    public static void init() {
        if (instance != null) throw new IllegalStateException();
        instance = new ItemJEI();
        instance.updater.markReady(new ItemVisibilityUpdater.ItemContext());
    }
}
