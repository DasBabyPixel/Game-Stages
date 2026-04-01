package de.dasbabypixel.gamestages.neoforge.v1_21_1.addon;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import org.jspecify.annotations.NonNull;

public interface NeoAddon extends VAddon {
    default @NonNull NeoAddonKJS createKubeJSSupport() {
        return new NeoAddonKJS() {
        };
    }

    default @NonNull NeoAddonJEI createJEISupport() {
        return new NeoAddonJEI() {
        };
    }

    default @NonNull NeoAddonProbeJS createProbeJSSupport() {
        return new NeoAddonProbeJS() {
        };
    }

    default void initResources(@NonNull ReloadableServerResources serverResources, @NonNull RegistryAccess registryAccess) {
    }

    default void beforeRegisterEvent(@NonNull AbstractGameStageManager gameStageManager, @NonNull ReloadableServerResources serverResources, @NonNull RegistryAccess registryAccess) {
    }

    default void afterRegisterEvent(@NonNull AbstractGameStageManager gameStageManager, @NonNull ReloadableServerResources serverResources, @NonNull RegistryAccess registryAccess) {
    }
}
