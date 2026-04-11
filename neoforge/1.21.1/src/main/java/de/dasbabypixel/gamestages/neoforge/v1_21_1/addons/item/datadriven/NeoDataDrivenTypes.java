package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.VDataDrivenTypes;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenTypes;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NeoDataDrivenTypes {
    public static void register(DataDrivenTypes types, ItemStackRestrictionResolverFactories factories) {
        VDataDrivenTypes.register(types, factories);
    }
}
