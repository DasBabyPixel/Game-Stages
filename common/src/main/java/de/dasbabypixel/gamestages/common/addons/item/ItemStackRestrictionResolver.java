package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ItemStackRestrictionResolver {
    @Nullable CompiledItemStackRestrictionEntry resolveRestrictionEntry(@NonNull ItemStack itemStack);

    @NonNull List<@NonNull CompiledItemStackRestrictionEntry> restrictionEntries();
}
