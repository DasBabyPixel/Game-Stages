package de.dasbabypixel.gamestages.common.v1_21_1.addon;

import de.dasbabypixel.gamestages.common.addon.Addon;

public interface VAddon extends Addon {
    default void registerPackets(PacketRegistry registry) {
    }
}
