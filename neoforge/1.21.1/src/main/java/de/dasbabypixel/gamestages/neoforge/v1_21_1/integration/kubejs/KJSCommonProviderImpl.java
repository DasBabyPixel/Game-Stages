package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.integration.kubejs.KJSCommonProvider;
import de.dasbabypixel.gamestages.common.integration.kubejs.event.KJSEventGroup;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.StageAddedToPlayerEventJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.StageRemovedFromPlayerEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.KubeEvent;

public class KJSCommonProviderImpl implements KJSCommonProvider {
    @SuppressWarnings("DataFlowIssue")
    @Override
    public KJSEventGroup eventGroupOf(String name) {
        return (KJSEventGroup) (Object) EventGroup.of(name);
    }

    @Override
    public Class<? extends KubeEvent> stageAddedToPlayerEvent() {
        return StageAddedToPlayerEventJS.class;
    }

    @Override
    public Class<? extends KubeEvent> stageRemovedFromPlayerEvent() {
        return StageRemovedFromPlayerEventJS.class;
    }
}
