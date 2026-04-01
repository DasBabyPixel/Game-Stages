package de.dasbabypixel.gamestages.neoforge.v1_21_1.addon;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventJSBase;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventType;
import org.jspecify.annotations.NonNull;

public interface EventRegistry {
    <Event extends EventJSBase<Event>> @NonNull EventType<Event> get(Class<Event> cls);
}
