package de.dasbabypixel.gamestages.common.addon;

import de.dasbabypixel.gamestages.common.client.ClientPlayerStages;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import org.jspecify.annotations.NonNull;

public interface Addon {
    default void registerCustomContent(ContentRegistry registry) {
    }

    default void postCompile(@NonNull CompiledRestrictionEntry restrictionEntry) {
    }

    default void postCompileAll(@NonNull AbstractGameStageManager instance, @NonNull BaseStages stages) {
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
