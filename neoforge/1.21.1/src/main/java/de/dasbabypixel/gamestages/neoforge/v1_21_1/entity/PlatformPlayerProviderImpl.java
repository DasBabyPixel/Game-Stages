package de.dasbabypixel.gamestages.neoforge.v1_21_1.entity;

import de.dasbabypixel.gamestages.common.entity.PlatformPlayerProvider;
import de.dasbabypixel.gamestages.common.entity.Player;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Collection;
import java.util.Objects;

public class PlatformPlayerProviderImpl implements PlatformPlayerProvider {
    @Override
    public Collection<? extends Player> allPlayers() {
        return Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer()).getPlayerList().getPlayers();
    }
}
