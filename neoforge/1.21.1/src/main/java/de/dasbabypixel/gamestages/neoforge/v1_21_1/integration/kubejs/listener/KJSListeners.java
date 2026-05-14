package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.listener;

import de.dasbabypixel.gamestages.common.data.manager.mutable.ServerMutableGameStageManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.KJSStagesWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.StageEvents;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.ServerRegisterEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.stages.StageCreationEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class KJSListeners {
    public static void register() {
        NeoForge.EVENT_BUS.addListener(KJSListeners::handleStageCreation);
    }

    public static void postRegisterEvent(ServerMutableGameStageManager manager) {
        StageEvents.SERVER_REGISTER.post(ScriptType.SERVER, new ServerRegisterEventJS(manager));
    }

    private static void handleStageCreation(StageCreationEvent event) {
        Objects.requireNonNull(event);
        event.setPlayerStages(new KJSStagesWrapper(event.getEntity()));
    }
}
