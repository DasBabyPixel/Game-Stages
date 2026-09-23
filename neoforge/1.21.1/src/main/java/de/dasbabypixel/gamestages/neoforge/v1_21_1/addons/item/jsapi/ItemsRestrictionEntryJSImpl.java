package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jsapi;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.ItemContentWrapper;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record ItemsRestrictionEntryJSImpl(ItemRestrictionSettingsJS getSettings,
                                          PreparedRestrictionPredicate getPredicate,
                                          ItemContentWrapper getItems) implements ItemsRestrictionEntryJS {
}
