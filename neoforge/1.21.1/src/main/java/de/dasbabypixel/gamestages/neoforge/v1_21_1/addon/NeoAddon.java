package de.dasbabypixel.gamestages.neoforge.v1_21_1.addon;

import de.dasbabypixel.gamestages.common.data.server.MutableGameStageManager;
import de.dasbabypixel.gamestages.common.event.EventType;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface NeoAddon extends VAddon {
    EventType<RegisterEventData> BEFORE_REGISTER_EVENT = EventType.create();
    EventType<RegisterEventData> AFTER_REGISTER_EVENT = EventType.create();
    EventType<InitResourcesEvent> INIT_RESOURCES_EVENT = EventType.create();

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

    record InitResourcesEvent(ReloadableServerResources serverResources, RegistryAccess registryAccess) {
    }

    record RegisterEventData(MutableGameStageManager manager, ReloadableServerResources serverResources,
                             RegistryAccess registryAccess) {
    }
}
