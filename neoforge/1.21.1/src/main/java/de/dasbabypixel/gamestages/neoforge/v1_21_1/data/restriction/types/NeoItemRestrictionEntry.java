package de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.types.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.types.CommonItemRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.CommonItemRestrictionPacket;
import org.jspecify.annotations.NonNull;

public class NeoItemRestrictionEntry extends CommonItemRestrictionEntry<NeoItemRestrictionEntry, NeoItemRestrictionEntry.PreCompiled> {
    public NeoItemRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate, @NonNull RestrictionEntryOrigin origin, @NonNull GameContent targetItems) {
        super(predicate, origin, targetItems);
    }

    @Override
    public @NonNull CustomPacket createPacket(@NonNull ServerGameStageManager instance) {
        var items = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetItems(), CommonItemCollection.TYPE);
        return new CommonItemRestrictionPacket(predicate(), items, hideTooltip(), renderItemName(), hideInJEI());
    }

    @Override
    public NeoItemRestrictionEntry.@NonNull PreCompiled precompile(@NonNull AbstractGameStageManager instance) {
        var items = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetItems(), CommonItemCollection.TYPE);
        return new PreCompiled(items);
    }

    @Override
    public @NonNull CompiledRestrictionEntry compile(@NonNull AbstractGameStageManager instance, @NonNull PreCompiled preCompiled, @NonNull CompiledRestrictionPredicate predicate) {
        return new Compiled(origin(), preCompiled.items, predicate);
    }

    public record Compiled(@NonNull RestrictionEntryOrigin origin, @NonNull CommonItemCollection gameContent,
                           @NonNull CompiledRestrictionPredicate predicate) implements CompiledRestrictionEntry {
    }

    public record PreCompiled(@NonNull CommonItemCollection items) {
    }
}
