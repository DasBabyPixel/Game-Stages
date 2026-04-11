package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface ItemStackRestrictionResolver {
    @Nullable CompiledItemStackRestrictionEntry resolveRestrictionEntry(ItemStack itemStack);

    List<CompiledItemStackRestrictionEntry> restrictionEntries();
}
