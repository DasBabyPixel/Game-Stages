package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jsapi;

import de.dasbabypixel.gamestages.common.addons.item.ItemCollection;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record ItemsRestrictionEntryJSImpl(ItemRestrictionSettingsJS getSettings,
                                          PreparedRestrictionPredicate getPredicate,
                                          ItemCollection getItems) implements ItemsRestrictionEntryJS {
}
