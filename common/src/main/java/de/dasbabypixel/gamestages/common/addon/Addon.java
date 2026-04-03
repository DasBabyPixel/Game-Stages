package de.dasbabypixel.gamestages.common.addon;

import de.dasbabypixel.gamestages.common.client.ClientPlayerStages;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import org.jspecify.annotations.NonNull;

public interface Addon {
    default void registerCustomContent(@NonNull ContentRegistry registry) {
    }

    default void postCompile(@NonNull RecompilationTask recompilationTask, @NonNull CompiledRestrictionEntry restrictionEntry) {
    }

    default void preCompileAll(@NonNull RecompilationTask recompilationTask) {
    }

    default void postCompileAll(@NonNull RecompilationTask recompilationTask) {
    }

    default void preReload(@NonNull AbstractGameStageManager instance) {
    }

    default void postReload(@NonNull AbstractGameStageManager instance) {
    }

    default void clientPostSyncUnlockedStages(@NonNull ClientPlayerStages playerStages) {
    }

    default void onRegister(@NonNull AddonManager<? extends Addon> addonManager) {
    }
}
