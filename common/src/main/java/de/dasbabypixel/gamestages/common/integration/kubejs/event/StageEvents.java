package de.dasbabypixel.gamestages.common.integration.kubejs.event;

import de.dasbabypixel.gamestages.common.CommonInstances;

public class StageEvents {
    public static final KJSEventGroup GROUP = KJSEventGroup.of("StageEvents");

    public static final KJSEventHandler STAGE_ADDED = GROUP.server("added", () -> CommonInstances.kjsCommonProvider.stageAddedToPlayerEvent());
    public static final KJSEventHandler STAGE_REMOVED = GROUP.server("removed", () -> CommonInstances.kjsCommonProvider.stageRemovedFromPlayerEvent());
}
