package de.dasbabypixel.gamestages.neoforge.v1_21_1.addon;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;

public interface NeoAddon extends VAddon {
    default NeoAddonKJS createKubeJSSupport() {
        return new NeoAddonKJS() {
        };
    }

    default NeoAddonJEI createJEISupport() {
        return new NeoAddonJEI() {
        };
    }

    default NeoAddonProbeJS createProbeJSSupport() {
        return new NeoAddonProbeJS() {
        };
    }

    default void initResources(ReloadableServerResources serverResources, RegistryAccess registryAccess) {
    }

    default void beforeRegisterEvent(AbstractGameStageManager gameStageManager, ReloadableServerResources serverResources, RegistryAccess registryAccess) {
    }

    default void afterRegisterEvent(AbstractGameStageManager gameStageManager, ReloadableServerResources serverResources, RegistryAccess registryAccess) {
    }
}
