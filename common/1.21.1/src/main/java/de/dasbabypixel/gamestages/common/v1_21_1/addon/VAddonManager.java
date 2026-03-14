package de.dasbabypixel.gamestages.common.v1_21_1.addon;

import de.dasbabypixel.gamestages.common.addon.AddonManager;

public abstract class VAddonManager<Addon extends VAddon> extends AddonManager<Addon> {
    protected VAddonManager() {
    }

    public static VAddonManager<?> instance() {
        return (VAddonManager<?>) AddonManager.instance();
    }
}
