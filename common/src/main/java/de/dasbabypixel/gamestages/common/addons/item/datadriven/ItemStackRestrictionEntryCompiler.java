package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionPredicateCompiler;
import org.jspecify.annotations.NonNull;

public class ItemStackRestrictionEntryCompiler {
    public static CompiledItemStackRestrictionEntry compile(@NonNull RestrictionPredicateCompiler predicateCompiler, @NonNull ItemStackRestrictionEntry entry) {
        var compiledPredicate = predicateCompiler.compile(entry.predicate());
        return new CompiledItemStackRestrictionEntry() {
            private final @NonNull CompiledRestrictionPredicate predicate = compiledPredicate;

            @Override
            public @NonNull CompiledRestrictionPredicate predicate() {
                return predicate;
            }
        };
    }
}
