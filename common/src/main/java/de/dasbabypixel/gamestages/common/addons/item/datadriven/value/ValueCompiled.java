package de.dasbabypixel.gamestages.common.addons.item.datadriven.value;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledResolverAlgorithm;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record ValueCompiled(CompiledItemStackRestrictionEntry entry,
                            List<CompiledItemStackRestrictionEntry> entries) implements CompiledResolverAlgorithm {
    @Override
    public CompiledItemStackRestrictionEntry resolve(ItemStack itemStack) {
        return entry;
    }
}
