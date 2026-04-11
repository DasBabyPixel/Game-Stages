package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential.SequentialData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.value.ValueData;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.predicate.PredicateData;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenType;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenTypes;
import org.jspecify.annotations.NullMarked;

import static de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork.*;

@NullMarked
public class VDataDrivenTypes {
    public static void register(DataDrivenTypes types, ItemStackRestrictionResolverFactories factories) {
        types.register(new DataDrivenType<>(ItemStackRestrictionEntryReference.class, ItemStackRestrictionEntryReference.TYPE, ITEM_STACK_RESTRICTION_ENTRY_REFERENCE_STREAM_CODEC));
        types.register(new DataDrivenType<>(ValueData.class, ValueData.TYPE, VALUE_DATA_STREAM_CODEC));
        types.register(new DataDrivenType<>(PredicateData.class, PredicateData.TYPE, PREDICATE_DATA_STREAM_CODEC));
        types.register(new DataDrivenType<>(SequentialData.class, SequentialData.TYPE, SEQUENTIAL_DATA_STREAM_CODEC));
        types.register(new DataDrivenType<>(VItemStackRestrictionEntrySettings.class, VItemStackRestrictionEntrySettings.TYPE, V_ITEM_STACK_RESTRICTION_ENTRY_SETTINGS_STREAM_CODEC));
        types.register(new DataDrivenType<>(ItemStackRestrictionEntry.class, ItemStackRestrictionEntry.TYPE, ITEM_STACK_RESTRICTION_ENTRY_STREAM_CODEC));
    }
}
