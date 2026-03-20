package de.dasbabypixel.gamestages.common.entity;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

public interface PlatformPlayerProvider {
    @Nullable ClientPlayer clientSelfPlayer();

    @NonNull Collection<? extends ServerPlayer> allPlayers();
}
