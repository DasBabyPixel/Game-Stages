package de.dasbabypixel.gamestages.common.v1_21_1.addon;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.data.graph.ImplicitDependencyGraph;
import de.dasbabypixel.gamestages.common.data.server.MutableGameStageManager;
import de.dasbabypixel.gamestages.common.event.EventType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface VAddon extends Addon {
    EventType<RegisterPacketsEvent> REGISTER_PACKETS_EVENT = EventType.create();
    EventType<ReloadPreServerEvent> RELOAD_PRE_SERVER_EVENT = EventType.create();
    EventType<ReloadPostServerEvent> RELOAD_POST_SERVER_EVENT = EventType.create();
    EventType<PreCompileServerPrepareEvent> PRE_COMPILE_SERVER_PREPARE_EVENT = EventType.create();
    EventType<ServerBuildDependencyGraphEvent> SERVER_BUILD_DEPENDENCY_GRAPH_EVENT = EventType.create();

    /**
     * Used to register custom packet types
     */
    record RegisterPacketsEvent(PacketRegistry registry) {
    }

    record ReloadPreServerEvent(MutableGameStageManager manager, ReloadableServerResources serverResources,
                                RegistryAccess registryAccess) {
    }

    /**
     * Called after the server manager has been modified
     */
    record ReloadPostServerEvent(MutableGameStageManager manager, ReloadableServerResources serverResources,
                                 RegistryAccess registryAccess) {
    }

    record PreCompileServerPrepareEvent(MutableGameStageManager manager, ReloadableServerResources serverResources,
                                        RegistryAccess registryAccess) {
    }

    record ServerBuildDependencyGraphEvent(MutableGameStageManager manager, ImplicitDependencyGraph dependencyGraph,
                                           ReloadableServerResources serverResources, RegistryAccess registryAccess) {
    }
}
