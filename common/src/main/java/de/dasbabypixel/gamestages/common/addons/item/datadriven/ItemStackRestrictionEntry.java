package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import org.jspecify.annotations.NonNull;

public record ItemStackRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate,
                                        @NonNull ItemStackRestrictionEntrySettings settings) implements DataDrivenData {
}
