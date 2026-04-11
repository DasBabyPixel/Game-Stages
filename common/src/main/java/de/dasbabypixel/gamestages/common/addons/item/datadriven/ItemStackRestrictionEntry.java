package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record ItemStackRestrictionEntry(PreparedRestrictionPredicate predicate,
                                        ItemStackRestrictionEntrySettings settings) {
}
