package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.startup;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventJSBase;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class StartupRegisterEventJS extends EventJSBase<StartupRegisterEventJS> {
    public static final EventType<StartupRegisterEventJS> TYPE = new EventType<>(StartupRegisterEventJS.class);

    public StartupRegisterEventJS() {
        super(TYPE);
    }
}
