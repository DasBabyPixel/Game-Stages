package de.dasbabypixel.gamestages.common.integration.kubejs;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.GameStageReference;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.integration.kubejs.api.server.GameStagesJS;
import de.dasbabypixel.gamestages.common.integration.kubejs.binding.KJSBindingRegistry;
import de.dasbabypixel.gamestages.common.integration.kubejs.event.KJSEventGroupRegistry;
import de.dasbabypixel.gamestages.common.integration.kubejs.event.StageEvents;

public class GameStagesPlugin {
    public static void registerBindings(KJSBindingRegistry registry) {
        registry.register("GameStages", GameStagesJS.class);
        registry.register("GameStage", GameStage.class);
        registry.register("GameStageReference", GameStageReference.class);
    }

    public static void registerEvents(KJSEventGroupRegistry registry) {
        registry.register(StageEvents.GROUP);
    }

    public static void beforeScripts(KJSScriptType scriptType) {
        if (scriptType == KJSScriptType.SERVER) {
            ServerGameStageManager.instance().allowMutation();
            ServerGameStageManager.instance().reset();
        }
    }

    public static void afterScripts(KJSScriptType scriptType) {
        if (scriptType == KJSScriptType.SERVER) {
            ServerGameStageManager.instance().disallowMutation();
            if (ServerGameStageManager.INSTANCE != null) {
                ServerGameStageManager.INSTANCE.sync();
            }
        }
    }
}
