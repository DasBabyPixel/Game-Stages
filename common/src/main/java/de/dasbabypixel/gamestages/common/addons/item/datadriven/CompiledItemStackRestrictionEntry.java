package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import org.jspecify.annotations.NonNull;

public interface CompiledItemStackRestrictionEntry {
    @NonNull CompiledRestrictionPredicate predicate();
}
