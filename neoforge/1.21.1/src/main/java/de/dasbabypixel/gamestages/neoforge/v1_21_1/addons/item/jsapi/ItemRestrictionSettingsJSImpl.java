package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jsapi;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.settings.VItemStackRestrictionEntrySettings;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ItemRestrictionSettingsJSImpl implements ItemRestrictionSettingsJS {
    private final VItemStackRestrictionEntrySettings settings;

    public ItemRestrictionSettingsJSImpl(VItemStackRestrictionEntrySettings settings) {
        this.settings = settings;
    }

    @Override
    public ItemRestrictionSettingsJS copy() {
        return new ItemRestrictionSettingsJSImpl(settings.copy());
    }

    @Override
    public @Nullable String getHiddenName() {
        return settings.hiddenName().hiddenName() ? settings.hiddenName().function().reference() : null;
    }

    @Override
    public void setHiddenName(@Nullable String hiddenName) {
        if (hiddenName != null) {
            settings.hiddenName().setHiddenName(true);
            settings.hiddenName().setFunction(hiddenName);
        } else {
            settings.hiddenName().setHiddenName(false);
        }
    }
}
