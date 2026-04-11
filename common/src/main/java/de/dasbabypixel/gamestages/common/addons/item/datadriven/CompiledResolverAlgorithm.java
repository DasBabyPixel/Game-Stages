package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.data.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface CompiledResolverAlgorithm {
    /**
     * @param itemStack the ItemStack to resolve
     * @return the restriction entry for the ItemStack, null if not restricted
     */
    @Nullable CompiledItemStackRestrictionEntry resolve(ItemStack itemStack);

    List<CompiledItemStackRestrictionEntry> entries();
}
