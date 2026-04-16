package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionPredicateCompiler;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ItemStackRestrictionEntryCompiler {
    public static CompiledItemStackRestrictionEntry compile(RestrictionPredicateCompiler predicateCompiler, ItemStackRestrictionEntry entry) {
        var compiledPredicate = predicateCompiler.compile(entry.predicate());
        return new CompiledItemStackRestrictionEntry() {
            private final CompiledRestrictionPredicate predicate = compiledPredicate;

            @Override
            public ItemStackRestrictionEntry entry() {
                return entry;
            }

            @Override
            public CompiledRestrictionPredicate predicate() {
                return predicate;
            }
        };
    }
}
