package de.dasbabypixel.gamestages.common.addon;

import de.dasbabypixel.gamestages.common.event.EventType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ClientEvents {
    EventType<ClientEnableEvent> CLIENT_ENABLE = EventType.create();
    EventType<ClientDisableEvent> CLIENT_DISABLE = EventType.create();

    /**
     * Called when the client's stages logic should be disabled.
     */
    record ClientDisableEvent() {
    }

    /**
     * Called when the client's stages logic should be enabled
     */
    record ClientEnableEvent() {
    }
}
