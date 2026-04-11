package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record ItemStackRestrictionEntryReference(String referenceId) implements DataDrivenData {
    public static final String TYPE = "itemstack_restriction_entry_reference";
}
