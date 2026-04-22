package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.data.SequentialData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.data.ValueData;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.data.PredicateData;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenType;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenTypes;
import org.jspecify.annotations.NullMarked;

import static de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork.PREDICATE_DATA_STREAM_CODEC;
import static de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork.SEQUENTIAL_DATA_STREAM_CODEC;
import static de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork.VALUE_DATA_STREAM_CODEC;

@NullMarked
public class VDataDrivenTypes {
    public static void register(DataDrivenTypes types, ItemStackRestrictionResolverFactories factories) {
        types.register(new DataDrivenType<>(ValueData.class, ValueData.TYPE, VALUE_DATA_STREAM_CODEC));
        types.register(new DataDrivenType<>(PredicateData.class, PredicateData.TYPE, PREDICATE_DATA_STREAM_CODEC));
        types.register(new DataDrivenType<>(SequentialData.class, SequentialData.TYPE, SEQUENTIAL_DATA_STREAM_CODEC));
    }
}
