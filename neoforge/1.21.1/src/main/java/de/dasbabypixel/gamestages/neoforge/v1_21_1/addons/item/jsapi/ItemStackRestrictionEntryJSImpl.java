package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jsapi;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record ItemStackRestrictionEntryJSImpl(ItemRestrictionSettingsJS getSettings,
                                              PreparedRestrictionPredicate getPredicate,
                                              ItemStackRestrictionEntryReference reference) implements ItemStackRestrictionEntryJS {
}
