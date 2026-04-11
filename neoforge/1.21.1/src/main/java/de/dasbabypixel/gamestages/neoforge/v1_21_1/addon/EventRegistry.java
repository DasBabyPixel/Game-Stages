package de.dasbabypixel.gamestages.neoforge.v1_21_1.addon;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventJSBase;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface EventRegistry {
    <Event extends EventJSBase<Event>> EventType<Event> get(Class<Event> cls);
}
