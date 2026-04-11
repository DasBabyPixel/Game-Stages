package de.dasbabypixel.gamestages.common.addons.item.datadriven.value;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record ValueData(ItemStackRestrictionEntryReference restrictionEntryReference) implements DataDrivenData {
    public static final String TYPE = "value";
}
