package de.dasbabypixel.gamestages.neoforge.v1_21_1.addon;

import de.dasbabypixel.gamestages.common.data.server.MutableGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import org.jspecify.annotations.NullMarked;

@NullMarked
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

    default void beforeRegisterEvent(MutableGameStageManager gameStageManager, ReloadableServerResources serverResources, RegistryAccess registryAccess) {
    }

    default void afterRegisterEvent(MutableGameStageManager gameStageManager, ReloadableServerResources serverResources, RegistryAccess registryAccess) {
    }
}
