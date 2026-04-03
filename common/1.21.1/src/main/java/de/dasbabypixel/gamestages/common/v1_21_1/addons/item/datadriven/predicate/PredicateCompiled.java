package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.predicate;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledResolverAlgorithm;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import net.minecraft.advancements.critereon.ItemPredicate;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record PredicateCompiled(@NonNull CompiledItemStackRestrictionEntry entry,
                                @NonNull List<@NonNull CompiledItemStackRestrictionEntry> entries,
                                @NonNull ItemPredicate predicate) implements CompiledResolverAlgorithm {
    public PredicateCompiled(@NonNull CompiledItemStackRestrictionEntry entry, @NonNull ItemPredicate predicate) {
        this(entry, List.of(entry), predicate);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @Nullable CompiledItemStackRestrictionEntry resolve(ItemStack itemStack) {
        if (predicate.test((net.minecraft.world.item.ItemStack) (Object) itemStack)) {
            return entry;
        }
        return null;
    }
}
