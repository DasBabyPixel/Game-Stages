package de.dasbabypixel.gamestages.common.addons.item.datadriven.value;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledResolverAlgorithm;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record ValueCompiled(@NonNull CompiledItemStackRestrictionEntry entry,
                            @NonNull List<@NonNull CompiledItemStackRestrictionEntry> entries) implements CompiledResolverAlgorithm {
    @Override
    public @NonNull CompiledItemStackRestrictionEntry resolve(ItemStack itemStack) {
        return entry;
    }
}
