package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntrySettings;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jspecify.annotations.NonNull;

public abstract class VItemStackRestrictionEntrySettings implements ItemStackRestrictionEntrySettings {
    public VItemStackRestrictionEntrySettings() {
    }

    public VItemStackRestrictionEntrySettings(@NonNull RegistryFriendlyByteBuf buf) {
        this();
    }

    public void encode(@NonNull RegistryFriendlyByteBuf buf) {
    }
}
