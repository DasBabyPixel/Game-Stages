package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.listener;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.KJSStagesWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.RegisterEventJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.StageEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.stages.StageCreationEvent;
import net.neoforged.neoforge.common.NeoForge;

public class KJSListeners {
    public static void register() {
        NeoForge.EVENT_BUS.addListener(KJSListeners::handleStageCreation);
    }

    public static void postRegisterEvent(AbstractGameStageManager instance) {
        StageEvents.REGISTER.post(ScriptType.SERVER, new RegisterEventJS(instance));
    }

    private static void handleStageCreation(StageCreationEvent event) {
        event.setPlayerStages(new KJSStagesWrapper(event.getEntity()));
    }
}
