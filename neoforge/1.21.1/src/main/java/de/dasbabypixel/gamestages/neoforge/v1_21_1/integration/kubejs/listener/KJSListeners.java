package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.listener;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.KJSStagesWrapper;
import dev.latvian.mods.kubejs.stages.StageCreationEvent;
import net.neoforged.neoforge.common.NeoForge;

public class KJSListeners {
    public static void register() {
        NeoForge.EVENT_BUS.addListener(KJSListeners::handleStageCreation);
    }

    private static void handleStageCreation(StageCreationEvent event) {
        event.setPlayerStages(new KJSStagesWrapper(event.getEntity()));
    }
}
