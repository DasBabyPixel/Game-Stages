package de.dasbabypixel.gamestages.common.addon;

import de.dasbabypixel.gamestages.common.client.ClientPlayerStages;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.server.MutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.PacketConsumer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Addon {
    default void onSyncConfigToPlayer(ServerGameStageManager instance, PacketConsumer packetConsumer) {
    }

    default void registerCustomContent(ContentRegistry registry) {
    }

    default void postCompile(RecompilationTask recompilationTask, CompiledRestrictionEntry restrictionEntry) {
    }

    default void preCompileAll(RecompilationTask recompilationTask) {
    }

    default void postCompileAll(RecompilationTask recompilationTask) {
    }

    default void preReload(MutableGameStageManager instance) {
    }

    default void postReload(MutableGameStageManager instance) {
    }

    default void clientPostSyncUnlockedStages(ClientPlayerStages playerStages) {
    }

    default void onRegister(AddonManager<? extends Addon> addonManager) {
    }
}
