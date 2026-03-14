package de.dasbabypixel.gamestages.common.addon;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.PlayerStages;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import org.jspecify.annotations.NonNull;

public interface Addon {
    default void registerCustomContent(ContentRegistry registry) {
    }

    default void postCompile(@NonNull CompiledRestrictionEntry restrictionEntry) {
    }

    default void postCompileAll(@NonNull AbstractGameStageManager instance, @NonNull PlayerStages stages) {
    }

    default void clientPostSyncUnlockedStages(PlayerStages playerStages) {
    }
}
