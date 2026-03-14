package de.dasbabypixel.gamestages.common.addon;

import java.util.ArrayList;
import java.util.List;

public abstract class AddonManager<Addon extends de.dasbabypixel.gamestages.common.addon.Addon> {
    private static AddonManager<?> instance = null;
    private final List<Addon> addons = new ArrayList<>();
    protected boolean frozen = false;

    protected AddonManager() {
        if (instance != null) throw new IllegalStateException();
        instance = this;
    }

    public static AddonManager<?> instance() {
        return instance;
    }

    public List<Addon> addons() {
        if (!frozen) throw new UnsupportedOperationException();
        return addons;
    }

    protected void addAddon(Addon addon) {
        if (frozen) throw new UnsupportedOperationException("Frozen");
        this.addons.add(addon);
    }
}
