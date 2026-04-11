package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CompiledItemStackRestrictionEntry {
    CompiledRestrictionPredicate predicate();
}
