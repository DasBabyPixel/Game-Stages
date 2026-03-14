package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.listener;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.DuplicatesException;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryCompiler;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.KJSStagesWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.RegisterEventJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.StageEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.stages.StageCreationEvent;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class KJSListeners {
    public static void register() {
        NeoForge.EVENT_BUS.addListener(KJSListeners::handleStageCreation);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, KJSListeners::handleAddReloadListener);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, KJSListeners::handlePlayerJoin);
    }

    private static void handleAddReloadListener(AddReloadListenerEvent event) {
        var serverResources = event.getServerResources();
        var registryAccess = event.getRegistryAccess();
        for (var addon : NeoAddonManager.instance().addons()) {
            addon.initResources(serverResources, registryAccess);
        }
        event.addListener((ResourceManagerReloadListener) resourceManager -> {
            var instance = ServerGameStageManager.instance();
            instance.allowMutation();
            instance.reset();

            for (var addon : NeoAddonManager.instance().addons()) {
                addon.beforeRegisterEvent(instance, serverResources, registryAccess);
            }

            StageEvents.REGISTER.post(ScriptType.SERVER, new RegisterEventJS(instance));

            for (var addon : NeoAddonManager.instance().addons()) {
                addon.afterRegisterEvent(instance, serverResources, registryAccess);
            }
            instance.disallowMutation();
            if (ServerGameStageManager.INSTANCE != null) {
                pushUpdate(ServerGameStageManager.INSTANCE);
            }
        });
    }

    private static void handlePlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        KJSListeners.pushUpdate(Objects.requireNonNull(ServerGameStageManager.INSTANCE));
    }

    private static void pushUpdate(@NonNull ServerGameStageManager instance) {
        var restrictionEntryCompiler = instance.get(RestrictionEntryCompiler.ATTRIBUTE);
        for (var restriction : instance.restrictions()) {
            restrictionEntryCompiler.precompile(restriction);
        }

        try {
            CommonInstances.platformPlayerProvider
                    .allPlayers()
                    .forEach(p -> p.getGameStages().recompileAll(restrictionEntryCompiler));
        } catch (DuplicatesException d) {
            d.print(System.err::println);
            System.err.println("Failed GameStages reload because of duplicates");
        }
        instance.sync(CommonInstances.platformPacketDistributor::sendToAllPlayers);
        CommonInstances.platformPlayerProvider.allPlayers().forEach(p -> p.getGameStages().fullSync());
    }

    private static void handleStageCreation(StageCreationEvent event) {
        event.setPlayerStages(new KJSStagesWrapper(event.getEntity()));
    }
}
