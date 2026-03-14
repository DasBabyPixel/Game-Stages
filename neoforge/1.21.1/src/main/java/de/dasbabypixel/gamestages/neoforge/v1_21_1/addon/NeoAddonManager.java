package de.dasbabypixel.gamestages.neoforge.v1_21_1.addon;

import de.dasbabypixel.gamestages.common.BuildConstants;
import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddonManager;
import net.neoforged.fml.InterModComms;

import java.util.Objects;
import java.util.function.Supplier;

public class NeoAddonManager extends VAddonManager<NeoAddon> {
    private static NeoAddonManager INSTANCE;

    private NeoAddonManager() {
        INSTANCE = this;
    }

    public static NeoAddonManager instance() {
        return INSTANCE;
    }

    public static void init() {
        new NeoAddonManager();
    }

    public static void done() {
        Objects.requireNonNull(INSTANCE).frozen = true;
    }

    public static void registerAddon(Supplier<? extends Addon> addonSupplier) {
        InterModComms.sendTo(BuildConstants.MOD_ID, "register_addon", addonSupplier);
    }

    @Override
    public void addAddon(NeoAddon addon) {
        super.addAddon(addon);
    }
}
