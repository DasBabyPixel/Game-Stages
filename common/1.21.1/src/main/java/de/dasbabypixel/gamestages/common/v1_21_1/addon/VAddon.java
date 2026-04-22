package de.dasbabypixel.gamestages.common.v1_21_1.addon;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.data.attribute.AttributeQuery;
import de.dasbabypixel.gamestages.common.data.manager.mutable.AbstractMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ServerMutableGameStageManager;
import de.dasbabypixel.gamestages.common.event.EventType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface VAddon extends Addon {
    EventType<RegisterPacketsEvent> REGISTER_PACKETS_EVENT = EventType.create();

    AttributeQuery.Holder<ServerMutableGameStageManager, ReloadableServerResources> SERVER_RESOURCES_ATTRIBUTE = AttributeQuery.holder();
    AttributeQuery.Holder<AbstractMutableGameStageManager<?>, RegistryAccess> REGISTRY_ATTRIBUTE = AttributeQuery.holder();

    /**
     * Used to register custom packet types
     */
    record RegisterPacketsEvent(PacketRegistry registry) {
    }
}
