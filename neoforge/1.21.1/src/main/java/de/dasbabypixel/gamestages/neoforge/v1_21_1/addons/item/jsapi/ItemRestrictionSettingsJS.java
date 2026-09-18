package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jsapi;

import de.dasbabypixel.gamestages.common.data.manager.mutable.ServerMutableGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.settings.VItemStackRestrictionEntrySettings;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface ItemRestrictionSettingsJS {
    static ItemRestrictionSettingsJS create(ServerMutableGameStageManager manager) {
        return new ItemRestrictionSettingsJSImpl(VItemStackRestrictionEntrySettings.create(manager));
    }

    ItemRestrictionSettingsJS copy();

    @Nullable String getHiddenName();

    void setHiddenName(@Nullable String hiddenName);
}
