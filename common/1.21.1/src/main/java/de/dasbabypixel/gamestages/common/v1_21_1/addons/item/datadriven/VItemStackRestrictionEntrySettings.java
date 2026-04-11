package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntrySettings;

public class VItemStackRestrictionEntrySettings implements ItemStackRestrictionEntrySettings {
    public static final String TYPE = "itemstack_restriction_entry_settings";
    private static final VItemStackRestrictionEntrySettings instance = new VItemStackRestrictionEntrySettings();

    private VItemStackRestrictionEntrySettings() {
    }

    public static VItemStackRestrictionEntrySettings instance() {
        return instance;
    }
}
