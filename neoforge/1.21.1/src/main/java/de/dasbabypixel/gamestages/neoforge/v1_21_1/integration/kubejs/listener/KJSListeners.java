package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.listener;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryCompiler;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.KJSStagesWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.RegisterEventJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.StageEvents;
import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.stages.StageCreationEvent;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.jspecify.annotations.NonNull;

public class KJSListeners {
    public static void register() {
        NeoForge.EVENT_BUS.addListener(KJSListeners::handleStageCreation);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, KJSListeners::handleAddReloadListener);
    }

    private static void handleAddReloadListener(AddReloadListenerEvent event) {
        var serverResources = ((ReloadableServerResourcesKJS) event.getServerResources());
        event.addListener((ResourceManagerReloadListener) resourceManager -> {
            var scriptManager = serverResources.kjs$getServerScriptManager();
            var registries = scriptManager.getRegistries();
            if (ServerGameStageManager.INSTANCE != null) {
                ServerGameStageManager.INSTANCE.allowMutation();
            }
            StageEvents.REGISTER.post(ScriptType.SERVER, new RegisterEventJS(registries, ServerGameStageManager.instance()));
            if (ServerGameStageManager.INSTANCE != null) {
                ServerGameStageManager.INSTANCE.disallowMutation();

                pushUpdate(ServerGameStageManager.INSTANCE);
            }
        });
    }

    private static void pushUpdate(@NonNull ServerGameStageManager instance) {
        var restrictionEntryCompiler = instance.get(RestrictionEntryCompiler.ATTRIBUTE);
        for (var restriction : instance.restrictions()) {
            restrictionEntryCompiler.precompile(restriction);
        }

        CommonInstances.platformPlayerProvider
                .allPlayers()
                .forEach(p -> p.getGameStages().recompileAll(restrictionEntryCompiler));
        instance.sync(CommonInstances.platformPacketDistributor::sendToAllPlayers);
    }

    private static void handleStageCreation(StageCreationEvent event) {
        event.setPlayerStages(new KJSStagesWrapper(event.getEntity()));
    }
}
