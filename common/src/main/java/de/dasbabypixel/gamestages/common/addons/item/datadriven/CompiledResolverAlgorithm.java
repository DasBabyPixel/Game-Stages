package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.data.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface CompiledResolverAlgorithm {
    /**
     * @param itemStack the ItemStack to resolve
     * @return the restriction entry for the ItemStack, null if not restricted
     */
    @Nullable CompiledItemStackRestrictionEntry resolve(ItemStack itemStack);

    @NonNull List<@NonNull CompiledItemStackRestrictionEntry> entries();
}
