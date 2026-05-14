package de.dasbabypixel.gamestages.neoforge.v1_21_1;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.addon.Addon.ReloadPostEvent;
import de.dasbabypixel.gamestages.common.addon.Addon.ReloadPreEvent;
import de.dasbabypixel.gamestages.common.data.DuplicatesException;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ServerMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.SimpleMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.server.GlobalServerState;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon.RegisterEventData;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.listener.KJSListeners;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ReloadableServerResources;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static de.dasbabypixel.gamestages.common.addon.Addon.RELOAD_POST_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.RELOAD_PRE_EVENT;
import static de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon.REGISTRY_ATTRIBUTE;
import static de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon.SERVER_RESOURCES_ATTRIBUTE;
import static de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon.AFTER_REGISTER_EVENT;
import static de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon.BEFORE_REGISTER_EVENT;
import static de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon.INIT_RESOURCES_EVENT;

@NullMarked
public class ReloadHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReloadHandler.class);
    private static final List<String> PENDING_DUPLICATES = new ArrayList<>();
    private static final AtomicInteger VERSION_COUNTER = new AtomicInteger();

    public static void registerListeners() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ReloadHandler::handlePlayerJoin);
    }

    public static void fullReload(ReloadableServerResources serverResources, RegistryAccess registryAccess) {
        var version = VERSION_COUNTER.incrementAndGet();
        PENDING_DUPLICATES.clear();
        INIT_RESOURCES_EVENT.call(new NeoAddon.InitResourcesEvent(serverResources, registryAccess));
        var manager = new ServerMutableGameStageManager();
        manager.init(SimpleMutableGameStageManager.VERSION, version);
        manager.init(SERVER_RESOURCES_ATTRIBUTE, serverResources);
        manager.init(REGISTRY_ATTRIBUTE, registryAccess);

        RELOAD_PRE_EVENT.call(new ReloadPreEvent(manager));

        if (Mods.KUBEJS.isLoaded()) {
            BEFORE_REGISTER_EVENT.call(new RegisterEventData(manager));
            KJSListeners.postRegisterEvent(manager);
            AFTER_REGISTER_EVENT.call(new RegisterEventData(manager));
        }

        RELOAD_POST_EVENT.call(new ReloadPostEvent(manager));

        ServerGameStageManager immutableManager;
        try {
            immutableManager = manager.compile();
        } catch (DuplicatesException exception) {
            exception.print(s -> {
                LOGGER.error(s);
                PENDING_DUPLICATES.add(s);
            });
            manager = new ServerMutableGameStageManager();
            manager.init(SimpleMutableGameStageManager.VERSION, version);
            manager.init(SERVER_RESOURCES_ATTRIBUTE, serverResources);
            manager.init(REGISTRY_ATTRIBUTE, registryAccess);
            RELOAD_PRE_EVENT.call(new ReloadPreEvent(manager));
            RELOAD_POST_EVENT.call(new ReloadPostEvent(manager));
            immutableManager = manager.compile();
        }
        GlobalServerState.updateManager(immutableManager);
        pushFullUpdate(immutableManager);
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

        net.minecraft.server.level.ServerPlayer sp = (net.minecraft.server.level.ServerPlayer) player;
        for (var pendingDuplicate : PENDING_DUPLICATES) {
            sp.sendSystemMessage(Component.literal(pendingDuplicate).withStyle(ChatFormatting.RED));
        }
    }

    public static void pushFullUpdate(ServerGameStageManager manager) {
        var players = CommonInstances.platformPlayerProvider.allPlayers();
        if (!players.isEmpty()) {
            manager.sync(CommonInstances.platformPacketDistributor::sendToAllPlayers);
            for (var player : CommonInstances.platformPlayerProvider.allPlayers()) {
                playerUpdate(manager, player);
            }
        }
        if (GlobalServerState.initialized()) {
            GlobalServerState.state().stagesCache().recompileComposite(manager);
        }
    }
}
