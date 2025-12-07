package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.v1_21_1.data.GameContentFlattener;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types.NeoItemRestrictionEntry;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import org.jspecify.annotations.NonNull;

public final class RegisterEventJS implements KubeEvent {
    private final @NonNull RegistryAccessContainer registries;
    private final @NonNull AbstractGameStageManager stageManager;
    private final GameContentFlattener flattener;
    private final JSParser parser = new JSParser();

    public RegisterEventJS(@NonNull RegistryAccessContainer registries, @NonNull AbstractGameStageManager stageManager) {
        this.registries = registries;
        this.stageManager = stageManager;
        this.flattener = stageManager.get(GameContentFlattener.ATTRIBUTE);
    }

    public @NonNull GameStage registerStage(@NonNull String stageName) {
        var stage = new GameStage(stageName);
        stageManager.add(stage);
        return stage;
    }

    public @NonNull GameContent items(@NonNull Context cx, @NonNull Object @NonNull ... items) {
        return parser.parseItems(cx, items);
    }

    public @NonNull NeoItemRestrictionEntry restrictItems(@NonNull Context cx, @NonNull PreparedRestrictionPredicate predicate, @NonNull Object @NonNull ... items) {
        var itemsContent = parser.parseItems(cx, items);
        return stageManager.addRestriction(new NeoItemRestrictionEntry(predicate, itemsContent));
    }
}
