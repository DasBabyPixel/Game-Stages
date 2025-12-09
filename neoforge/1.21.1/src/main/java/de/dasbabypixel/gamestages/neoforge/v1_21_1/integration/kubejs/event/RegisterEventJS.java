package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.types.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types.NeoFluidRestrictionEntry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types.NeoItemRestrictionEntry;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.SourceLine;
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
        this.flattener = stageManager.get(GameContentFlattener.Attribute.INSTANCE);
    }

    public @NonNull GameStage registerStage(@NonNull String stageName) {
        var stage = new GameStage(stageName);
        stageManager.add(stage);
        return stage;
    }

    public @NonNull GameContent items(@NonNull Context cx, @NonNull Object @NonNull ... items) {
        return parser.parseItems(cx, items);
    }

    public @NonNull GameContent fluids(@NonNull Context cx, @NonNull Object @NonNull ... fluids) {
        return parser.parseFluids(cx, fluids);
    }

    public @NonNull GameContent mods(@NonNull Context cx, @NonNull String @NonNull ... mods) {
        return parser.parseMods(cx, mods);
    }

    public @NonNull NeoItemRestrictionEntry restrictItems(@NonNull Context cx, @NonNull PreparedRestrictionPredicate predicate, @NonNull Object @NonNull ... items) {
        var itemsContent = parser.parseItems(cx, items);
        var source = SourceLine.of(cx).toString();
        return stageManager.addRestriction(new NeoItemRestrictionEntry(predicate, RestrictionEntryOrigin.string(source), itemsContent));
    }

    public @NonNull NeoFluidRestrictionEntry restrictFluids(@NonNull Context cx, @NonNull PreparedRestrictionPredicate predicate, @NonNull Object @NonNull ... items) {
        var fluidsContent = parser.parseFluids(cx, items);
        var source = SourceLine.of(cx).toString();
        return stageManager.addRestriction(new NeoFluidRestrictionEntry(predicate, RestrictionEntryOrigin.string(source), fluidsContent));
    }
}
