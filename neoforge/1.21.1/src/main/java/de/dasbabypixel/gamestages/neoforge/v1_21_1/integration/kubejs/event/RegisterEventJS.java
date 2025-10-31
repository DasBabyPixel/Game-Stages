package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.ItemCollection;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types.NeoItemRestrictionEntry;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import org.jspecify.annotations.NonNull;

public record RegisterEventJS(@NonNull RegistryAccessContainer registries,
                              @NonNull AbstractGameStageManager stageManager) implements KubeEvent {
    public @NonNull GameStage registerStage(@NonNull String stageName) {
        var stage = new GameStage(stageName);
        stageManager.add(stage);
        return stage;
    }

    public @NonNull NeoItemRestrictionEntry register(@NonNull PreparedRestrictionPredicate predicate, @NonNull ItemCollection<? extends CommonItemCollection<?>> itemCollection) {
        return stageManager.addRestriction(new NeoItemRestrictionEntry(predicate, itemCollection));
    }
}
