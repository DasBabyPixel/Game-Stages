package de.dasbabypixel.gamestages.neoforge.v1_21_1;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.DuplicatesException;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryPreCompiler;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.listener.KJSListeners;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class ReloadHandler {
    public static void registerListeners() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ReloadHandler::handleAddReloadListener);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ReloadHandler::handlePlayerJoin);
    }

    private static void handleAddReloadListener(AddReloadListenerEvent event) {
        Objects.requireNonNull(event);
        var serverResources = event.getServerResources();
        var registryAccess = event.getRegistryAccess();
        for (var addon : NeoAddonManager.instance().addons()) {
            addon.initResources(serverResources, registryAccess);
        }
        event.addListener((ResourceManagerReloadListener) resourceManager -> fullReload(serverResources, registryAccess));
    }

    public static void fullReload(@NonNull ReloadableServerResources serverResources, @NonNull RegistryAccess registryAccess) {
        var instance = ServerGameStageManager.instance();

        for (var addon : NeoAddonManager.instance().addons()) {
            addon.preReload(instance);
        }

        instance.allowMutation();
        instance.reset();

        for (var addon : NeoAddonManager.instance().addons()) {
            addon.beforeRegisterEvent(instance, serverResources, registryAccess);
        }

        if (Mods.KUBEJS.isLoaded()) {
            KJSListeners.postRegisterEvent(instance);
        }

        for (var addon : NeoAddonManager.instance().addons()) {
            addon.afterRegisterEvent(instance, serverResources, registryAccess);
        }
        instance.disallowMutation();

        for (var addon : NeoAddonManager.instance().addons()) {
            addon.postReload(instance);
        }

        if (ServerGameStageManager.INSTANCE != null) {
            pushFullUpdate(ServerGameStageManager.INSTANCE);
        }
    }

    private static void handlePlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Objects.requireNonNull(event);
        var player = (ServerPlayer) event.getEntity();
        var instance = Objects.requireNonNull(ServerGameStageManager.INSTANCE);
        instance.sync(packet -> CommonInstances.platformPacketDistributor.sendToPlayer(player, packet));
        playerUpdate(instance, player);
    }

    private static void playerUpdate(@NonNull ServerGameStageManager instance, @NonNull ServerPlayer player) {
        try {
            player.getGameStages().recompileAll(instance);
        } catch (DuplicatesException d) {
            NeoForgeEntrypoint.LOGGER.error("Failed GameStages reload because of duplicates", d);
        }
        player.getGameStages().fullSync();
    }

    public static void pushFullUpdate(@NonNull ServerGameStageManager instance) {
        var preCompiler = instance.get(RestrictionEntryPreCompiler.ATTRIBUTE);
        for (var restriction : instance.restrictions()) {
            preCompiler.precompile(restriction);
        }
        instance.sync(CommonInstances.platformPacketDistributor::sendToAllPlayers);
        for (var player : CommonInstances.platformPlayerProvider.allPlayers()) {
            playerUpdate(instance, player);
        }
    }
}
