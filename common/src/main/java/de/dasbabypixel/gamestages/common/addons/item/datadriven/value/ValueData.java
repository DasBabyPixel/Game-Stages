package de.dasbabypixel.gamestages.common.addons.item.datadriven.value;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import org.jspecify.annotations.NonNull;

public record ValueData(
        @NonNull ItemStackRestrictionEntryReference restrictionEntryReference) implements DataDrivenData {
}
