package de.dasbabypixel.gamestages.common.v1_21_1.addon;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface VAddon extends Addon {
    default void registerPackets(PacketRegistry registry) {
    }

    default void postReloadServer(AbstractGameStageManager<?> gameStageManager, ReloadableServerResources serverResources, RegistryAccess registryAccess) {
    }
}
