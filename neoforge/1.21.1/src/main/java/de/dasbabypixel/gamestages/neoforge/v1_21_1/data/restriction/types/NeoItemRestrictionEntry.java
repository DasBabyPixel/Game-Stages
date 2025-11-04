package de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types;

import de.dasbabypixel.gamestages.common.data.ItemCollection;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.compiled.ItemCollectionCompiler;
import de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.types.CommonItemRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.CommonItemRestrictionPacket;
import org.jspecify.annotations.NonNull;

public class NeoItemRestrictionEntry extends CommonItemRestrictionEntry<NeoItemRestrictionEntry, NeoItemRestrictionEntry.PreCompiled> {
    public NeoItemRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate, @NonNull ItemCollection<?> targetCollection) {
        super(predicate, targetCollection);
    }

    @Override
    public @NonNull CustomPacket createPacket(@NonNull ServerGameStageManager instance) {
        var items = instance.get(ItemCollectionCompiler.ATTRIBUTE).flatten(targetCollection());
        return new CommonItemRestrictionPacket(predicate(), items, hideTooltip(), renderItemName(), hideInJEI());
    }

    @Override
    public NeoItemRestrictionEntry.@NonNull PreCompiled precompile(@NonNull ServerGameStageManager instance) {
        var items = instance.get(ItemCollectionCompiler.ATTRIBUTE).flatten(targetCollection());
        return new PreCompiled(items);
    }

    @Override
    public @NonNull CompiledRestrictionEntry compile(@NonNull ServerGameStageManager instance, @NonNull PreCompiled preCompiled, @NonNull CompiledRestrictionPredicate predicate) {
        return null;
    }

    public record PreCompiled(CommonItemCollection.@NonNull Direct items) {
    }
}
