package de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledResolverAlgorithm;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public record SequentialCompiled(@NonNull List<@NonNull CompiledResolverAlgorithm> subAlgorithms,
                                 @NonNull List<@NonNull CompiledItemStackRestrictionEntry> entries) implements CompiledResolverAlgorithm {
    public SequentialCompiled {
        subAlgorithms = Objects.requireNonNull(List.copyOf(subAlgorithms));
        entries = Objects.requireNonNull(List.copyOf(entries));
    }

    @Override
    public @Nullable CompiledItemStackRestrictionEntry resolve(ItemStack itemStack) {
        for (var subAlgorithm : subAlgorithms) {
            var entry = subAlgorithm.resolve(itemStack);
            if (entry != null) return entry;
        }
        return null;
    }
}
