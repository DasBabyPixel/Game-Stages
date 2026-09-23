package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.listener;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ServerMutableGameStageManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSContext;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.KJSStagesWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.StagesKubeJSPlugin;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.StageEvents;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.ServerRegisterEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.stages.StageCreationEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@NullMarked
public class KJSListeners {
    private static final Logger LOGGER = Objects.requireNonNull(LoggerFactory.getLogger(KJSListeners.class));

    public static void register() {
        NeoForge.EVENT_BUS.addListener(KJSListeners::handleStageCreation);
    }

    public static void postRegisterEvent(ServerMutableGameStageManager manager) {
        var contextFactory = Objects.requireNonNull(Objects
                .requireNonNull(Objects.requireNonNull(ScriptType.SERVER.console).contextFactory)
                .get());
        var context = Objects.requireNonNull(contextFactory.enter());
        JSContext.initInstance(context);
        var cx = JSContext.instance(context);
        var contentTypes = new JSContext.ContentTypes(CommonInstances.gameContentRegistry
                .entries()
                .stream()
                .map(StagesKubeJSPlugin::getTypeEntry)
                .toList());
        cx.init(JSContext.Attributes.CONTENT_TYPES, contentTypes);
        cx.init(JSContext.Attributes.STAGE_MANAGER, manager);
        StageEvents.SERVER_REGISTER.post(ScriptType.SERVER, new ServerRegisterEventJS(manager));
        JSContext.clearInstance(context);
    }

    private static void handleStageCreation(StageCreationEvent event) {
        Objects.requireNonNull(event);
        event.setPlayerStages(new KJSStagesWrapper(event.getEntity()));
    }
}
