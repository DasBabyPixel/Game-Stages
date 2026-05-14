package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.ServerRegisterEventJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.startup.StartupRegisterEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class StageEvents {
    public static final EventGroup GROUP = EventGroup.of("StageEvents");

    public static final EventHandler STARTUP_REGISTER = GROUP.startup("startupRegister", () -> StartupRegisterEventJS.class);
    public static final EventHandler SERVER_REGISTER = GROUP.server("serverRegister", () -> ServerRegisterEventJS.class);
}
