package de.dasbabypixel.gamestages.common.integration.kubejs;

import de.dasbabypixel.gamestages.common.integration.kubejs.event.KJSEventGroup;
import dev.latvian.mods.kubejs.event.KubeEvent;

public interface KJSCommonProvider {
    KJSEventGroup eventGroupOf(String name);

    Class<? extends KubeEvent> stageAddedToPlayerEvent();

    Class<? extends KubeEvent> stageRemovedFromPlayerEvent();
}
