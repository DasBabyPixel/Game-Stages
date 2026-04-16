package de.dasbabypixel.gamestages.neoforge.v1_21_1;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.addon.Addon.ReloadPostEvent;
import de.dasbabypixel.gamestages.common.addon.Addon.ReloadPreEvent;
import de.dasbabypixel.gamestages.common.data.DuplicatesException;
import de.dasbabypixel.gamestages.common.data.graph.CompiledImplicitDependencyGraph;
import de.dasbabypixel.gamestages.common.data.graph.ImplicitDependencyGraph;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon.PreCompileServerPrepareEvent;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon.ReloadPostServerEvent;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon.ReloadPreServerEvent;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon.ServerBuildDependencyGraphEvent;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon.InitResourcesEvent;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon.RegisterEventData;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.listener.KJSListeners;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

import static de.dasbabypixel.gamestages.common.addon.Addon.RELOAD_POST_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.RELOAD_PRE_EVENT;
import static de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon.PRE_COMPILE_SERVER_PREPARE_EVENT;
import static de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon.RELOAD_POST_SERVER_EVENT;
import static de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon.RELOAD_PRE_SERVER_EVENT;
import static de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon.SERVER_BUILD_DEPENDENCY_GRAPH_EVENT;
import static de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon.AFTER_REGISTER_EVENT;
import static de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon.BEFORE_REGISTER_EVENT;
import static de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon.INIT_RESOURCES_EVENT;

@NullMarked
public class ReloadHandler {
    public static void registerListeners() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ReloadHandler::handleAddReloadListener);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ReloadHandler::handlePlayerJoin);
    }

    private static void handleAddReloadListener(AddReloadListenerEvent event) {
        var serverResources = event.getServerResources();
        var registryAccess = event.getRegistryAccess();
        INIT_RESOURCES_EVENT.call(new InitResourcesEvent(serverResources, registryAccess));
        event.addListener((ResourceManagerReloadListener) resourceManager -> fullReload(serverResources, registryAccess));
    }

    public static void fullReload(ReloadableServerResources serverResources, RegistryAccess registryAccess) {
        var manager = ServerGameStageManager.instance();

        RELOAD_PRE_SERVER_EVENT.call(new ReloadPreServerEvent(manager, serverResources, registryAccess));
        RELOAD_PRE_EVENT.call(new ReloadPreEvent(manager));

        manager.allowMutation();
        manager.reset();


        if (Mods.KUBEJS.isLoaded()) {
            BEFORE_REGISTER_EVENT.call(new RegisterEventData(manager, serverResources, registryAccess));
            KJSListeners.postRegisterEvent(manager);
            AFTER_REGISTER_EVENT.call(new RegisterEventData(manager, serverResources, registryAccess));
        }

        // Build dependency graph
        var dependencyGraph = new ImplicitDependencyGraph();
        SERVER_BUILD_DEPENDENCY_GRAPH_EVENT.call(new ServerBuildDependencyGraphEvent(manager, dependencyGraph, serverResources, registryAccess));
        var compiledGraph = dependencyGraph.compile();
        manager.get(CompiledImplicitDependencyGraph.Holder.ATTRIBUTE).graph = compiledGraph;
        for (var entry : compiledGraph.compiledMap().entrySet()) {
            Objects.requireNonNull(entry);
            var content = entry.getKey();
            var compiled = entry.getValue();
            var predicate = compiled.predicate();
        }

        PRE_COMPILE_SERVER_PREPARE_EVENT.call(new PreCompileServerPrepareEvent(manager, serverResources, registryAccess));
        manager.preparePrecompileRestrictions();
        manager.precompileRestrictions();

        RELOAD_POST_EVENT.call(new ReloadPostEvent(manager));
        RELOAD_POST_SERVER_EVENT.call(new ReloadPostServerEvent(manager, serverResources, registryAccess));

        manager.disallowMutation();

        if (ServerGameStageManager.INSTANCE != null) {
            pushFullUpdate(ServerGameStageManager.INSTANCE);
        }
    }

    private static void handlePlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        var player = (ServerPlayer) event.getEntity();
        var instance = Objects.requireNonNull(ServerGameStageManager.INSTANCE);
        instance.sync(packet -> CommonInstances.platformPacketDistributor.sendToPlayer(player, packet));
        playerUpdate(instance, player);
    }

    private static void playerUpdate(ServerGameStageManager instance, ServerPlayer player) {
        try {
            player.getGameStages().recompileAll(instance);
        } catch (DuplicatesException d) {
            NeoForgeEntrypoint.LOGGER.error("Failed GameStages reload because of duplicates", d);
        }
        player.getGameStages().fullSync();
    }

    public static void pushFullUpdate(ServerGameStageManager instance) {
        instance.sync(CommonInstances.platformPacketDistributor::sendToAllPlayers);
        for (var player : CommonInstances.platformPlayerProvider.allPlayers()) {
            playerUpdate(instance, player);
        }
    }
}
