package de.dasbabypixel.gamestages.common.entity;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface PlatformPlayerProvider {
    @Nullable ClientPlayer clientSelfPlayer();

    @NonNull Collection<? extends @NonNull ServerPlayer> allPlayers();

    @Nullable ServerPlayer getPlayer(@NonNull UUID uuid);
}
