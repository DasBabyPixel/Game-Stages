package de.dasbabypixel.gamestages.neoforge.v1_21_1;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.addon.Addon.ReloadPostEvent;
import de.dasbabypixel.gamestages.common.addon.Addon.ReloadPreEvent;
import de.dasbabypixel.gamestages.common.data.DuplicatesException;
import de.dasbabypixel.gamestages.common.data.graph.DependencyContent;
import de.dasbabypixel.gamestages.common.data.logicng.LogicNG;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ServerMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.compiler.ManagerCompilerTask;
import de.dasbabypixel.gamestages.common.data.server.GlobalServerState;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
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

import static de.dasbabypixel.gamestages.common.addon.Addon.RELOAD_POST_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.RELOAD_PRE_EVENT;
import static de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon.REGISTRY_ATTRIBUTE;
import static de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon.SERVER_RESOURCES_ATTRIBUTE;
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
        var manager = new ServerMutableGameStageManager();
        SERVER_RESOURCES_ATTRIBUTE.init(manager, serverResources);
        REGISTRY_ATTRIBUTE.init(manager, registryAccess);
        LogicNG.ATTRIBUTE.init(manager, new LogicNG());

        RELOAD_PRE_EVENT.call(new ReloadPreEvent(manager));

        if (Mods.KUBEJS.isLoaded()) {
            BEFORE_REGISTER_EVENT.call(new RegisterEventData(manager));
            KJSListeners.postRegisterEvent(manager);
            AFTER_REGISTER_EVENT.call(new RegisterEventData(manager));
        }

        RELOAD_POST_EVENT.call(new ReloadPostEvent(manager));

        var immutableManager = compile(manager);
        GlobalServerState.updateManager(immutableManager);
        pushFullUpdate(immutableManager);
    }

    private static String gv(DependencyContent content) {
        var cs = content.toString();
        var max = 5000;
        if (cs.length() > max) cs = cs.substring(0, max) + ".." + (cs.length() - max);
        return "\"" + cs + "\"";
    }

    private static ServerGameStageManager compile(ServerMutableGameStageManager manager) {
        var compilerTask = new ManagerCompilerTask(manager);
        compilerTask.precompileRestrictions();
        var restrictions = compilerTask.preCompileIndex().preCompiledRestrictions();
        var immutable = new ServerGameStageManager(manager.gameStages(), restrictions);
        LogicNG.ATTRIBUTE.init(immutable, manager.get(LogicNG.ATTRIBUTE));
        compilerTask.postCompile(immutable);
        return immutable;
    }

    private static void handlePlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        var player = (ServerPlayer) event.getEntity();
        var instance = GlobalServerState.currentManager();
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

    public static void pushFullUpdate(ServerGameStageManager manager) {
        manager.sync(CommonInstances.platformPacketDistributor::sendToAllPlayers);
        for (var player : CommonInstances.platformPlayerProvider.allPlayers()) {
            playerUpdate(manager, player);
        }
        if (GlobalServerState.initialized()) {
            GlobalServerState.state().stagesCache().recompileComposite(manager);
        }
    }
}
