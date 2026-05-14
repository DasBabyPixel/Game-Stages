package de.dasbabypixel.gamestages.common.addon;

import de.dasbabypixel.gamestages.common.client.ClientPlayerStages;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.PlayerCompilationTask;
import de.dasbabypixel.gamestages.common.data.attribute.CompilableAttributeHolder;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.SimpleMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.compiler.ManagerCompilerTask;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.event.EventType;
import de.dasbabypixel.gamestages.common.network.PacketConsumer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    EventType<CompileManagerEvent> COMPILE_MANAGER_EVENT = EventType.create();
    EventType<PreCompileTypeEvent> PRE_COMPILE_TYPE_EVENT = EventType.create();
    EventType<PostCompileTypeEvent> POST_COMPILE_TYPE_EVENT = EventType.create();
    EventType<PreCompilePrepareEvent> PRE_COMPILE_PREPARE_EVENT = EventType.create();
    EventType<ClientReplaceManagerEvent> CLIENT_REPLACE_MANAGER_EVENT = EventType.create();
    EventType<ClientRecompilePreEvent> CLIENT_RECOMPILE_PRE_EVENT = EventType.create();
    EventType<ClientRecompilePostEvent> CLIENT_RECOMPILE_POST_EVENT = EventType.create();

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
    record CompilePostEvent(PlayerCompilationTask playerCompilationTask,
                            CompiledRestrictionEntry<?, ?> restrictionEntry) {
    }

    /**
     * Called before a {@link BaseStages} instance is compiled
     */
    record CompileAllPreEvent(PlayerCompilationTask playerCompilationTask) {
    }

    /**
     * Called after a {@link BaseStages} instance has been compiled
     */
    record CompileAllPostEvent(PlayerCompilationTask playerCompilationTask) {
    }

    /**
     * Called before the manager is modified/reloaded
     */
    record ReloadPreEvent(SimpleMutableGameStageManager<?, ?> manager) {
    }

    /**
     * Called after the manager is modified/reloaded
     */
    record ReloadPostEvent(SimpleMutableGameStageManager<?, ?> manager) {
    }

    /**
     * Called on the client after the unlocked stages have been synced
     */
    record ClientPostSyncUnlockedStagesEvent(ClientPlayerStages playerStages) {
    }

    record CompileManagerEvent(ManagerCompilerTask task,
                               CompilableAttributeHolder.CompiledAttributesBuilder<? extends SimpleMutableGameStageManager<?, ?>, ? extends AbstractGameStageManager<?>> builder) {
    }

    record PreCompileTypeEvent(ManagerCompilerTask task, GameContentType<?> type) {
    }

    record PostCompileTypeEvent(ManagerCompilerTask task, GameContentType<?> type) {
    }

    record PreCompilePrepareEvent(ManagerCompilerTask task,
                                  Map<GameContentType<?>, List<GameContentType<?>>> evaluationDependencies) {
        public void addEvaluationDependency(GameContentType<?> content, GameContentType<?> dependency) {
            evaluationDependencies.computeIfAbsent(content, ignored -> new ArrayList<>()).add(dependency);
        }
    }

    record ClientReplaceManagerEvent(@Nullable ClientGameStageManager oldManager,
                                     @Nullable ClientGameStageManager newManager) {
    }

    record ClientRecompilePreEvent(ClientGameStageManager newManager, BaseStages stages) {
    }

    record ClientRecompilePostEvent(ClientGameStageManager newManager, BaseStages stages) {
    }
}
