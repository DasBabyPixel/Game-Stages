package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public class StageEvents {
    public static final EventGroup GROUP = EventGroup.of("StageEvents");

    public static final EventHandler STAGE_ADDED = GROUP.server("added", () -> StageAddedToPlayerEventJS.class);
    public static final EventHandler STAGE_REMOVED = GROUP.server("removed", () -> StageRemovedFromPlayerEventJS.class);
    public static final EventHandler REGISTER = GROUP.server("register", () -> RegisterEventJS.class);
}
