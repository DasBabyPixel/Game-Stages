package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jsapi;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;

public interface ItemStackRestrictionEntryJS {
    ItemRestrictionSettingsJS getSettings();

    PreparedRestrictionPredicate getPredicate();
}
