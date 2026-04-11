package de.dasbabypixel.gamestages.common.entity;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

@NullMarked
public interface PlatformPlayerProvider {
    @Nullable ClientPlayer clientSelfPlayer();

    Collection<? extends ServerPlayer> allPlayers();

    @Nullable ServerPlayer getPlayer(UUID uuid);
}
