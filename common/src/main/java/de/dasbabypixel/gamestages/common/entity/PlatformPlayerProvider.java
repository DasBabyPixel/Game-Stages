package de.dasbabypixel.gamestages.common.entity;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

public interface PlatformPlayerProvider {
    @Nullable Player clientSelfPlayer();

    @NonNull Collection<? extends Player> allPlayers();
}
