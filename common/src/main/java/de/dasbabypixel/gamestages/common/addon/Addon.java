package de.dasbabypixel.gamestages.common.addon;

import de.dasbabypixel.gamestages.common.client.ClientPlayerStages;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.event.EventType;
import de.dasbabypixel.gamestages.common.network.PacketConsumer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Addon {
    EventType<NetworkSyncConfigEvent> NETWORK_SYNC_CONFIG_EVENT = EventType.create();
    EventType<RegisterCustomContentEvent> REGISTER_CUSTOM_CONTENT_EVENT = EventType.create();
    EventType<CompilePostEvent> COMPILE_POST_EVENT = EventType.create();
    EventType<CompileAllPreEvent> COMPILE_ALL_PRE_EVENT = EventType.create();
    EventType<CompileAllPostEvent> COMPILE_ALL_POST_EVENT = EventType.create();
    EventType<ReloadPreEvent> RELOAD_PRE_EVENT = EventType.create();
    EventType<ReloadPostEvent> RELOAD_POST_EVENT = EventType.create();
    EventType<ClientPostSyncUnlockedStagesEvent> CLIENT_POST_SYNC_UNLOCKED_STAGES_EVENT = EventType.create();
    EventType<PreCompilePrepareEvent> PRE_COMPILE_PREPARE_EVENT = EventType.create();

    default void onRegister(AddonManager<? extends Addon> addonManager) {
    }

    /**
     * Called when the restriction configuration is synced to the client. Can be used to send additional packets for extra features
     */
    record NetworkSyncConfigEvent(ServerGameStageManager manager, PacketConsumer packetConsumer) {
    }

    /**
     * Called during initialization to register custom {@link GameContentType GameContentTypes}
     */
    record RegisterCustomContentEvent(ContentRegistry contentRegistry) {
    }

    /**
     * Called after a restriction entry has been compiled
     */
    record CompilePostEvent(RecompilationTask recompilationTask, CompiledRestrictionEntry<?, ?> restrictionEntry) {
    }

    /**
     * Called before a {@link BaseStages} instance is compiled
     */
    record CompileAllPreEvent(RecompilationTask recompilationTask) {
    }

    /**
     * Called after a {@link BaseStages} instance has been compiled
     */
    record CompileAllPostEvent(RecompilationTask recompilationTask) {
    }

    /**
     * Called before the manager is modified/reloaded
     */
    record ReloadPreEvent(AbstractGameStageManager<?> manager) {
    }

    /**
     * Called after the manager is modified/reloaded
     */
    record ReloadPostEvent(AbstractGameStageManager<?> manager) {
    }

    /**
     * Called on the client after the unlocked stages have been synced
     */
    record ClientPostSyncUnlockedStagesEvent(ClientPlayerStages playerStages) {
    }

    record PreCompilePrepareEvent(AbstractGameStageManager<?> manager) {
    }
}
