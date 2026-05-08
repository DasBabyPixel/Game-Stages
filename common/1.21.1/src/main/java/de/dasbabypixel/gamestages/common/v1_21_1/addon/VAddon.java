package de.dasbabypixel.gamestages.common.v1_21_1.addon;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.data.attribute.CompilableAttribute;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ServerMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.SimpleMutableGameStageManager;
import de.dasbabypixel.gamestages.common.event.EventType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface VAddon extends Addon {
    EventType<RegisterPacketsEvent> REGISTER_PACKETS_EVENT = EventType.create();

    CompilableAttribute<ServerMutableGameStageManager, ReloadableServerResources, ServerGameStageManager> SERVER_RESOURCES_ATTRIBUTE = CompilableAttribute.noop();
    CompilableAttribute<SimpleMutableGameStageManager<?, ?>, RegistryAccess, AbstractGameStageManager<?>> REGISTRY_ATTRIBUTE = CompilableAttribute.noop();

    /**
     * Used to register custom packet types
     */
    record RegisterPacketsEvent(PacketRegistry registry) {
    }
}
