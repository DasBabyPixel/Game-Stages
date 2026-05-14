package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.settings;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.settings.CompiledItemStackRestrictionEntrySettings;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record VCompiledItemStackRestrictionEntrySettings(
        VCompiledHiddenName hiddenName) implements CompiledItemStackRestrictionEntrySettings {
}
