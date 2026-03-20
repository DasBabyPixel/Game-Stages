package de.dasbabypixel.gamestages.common.entity;

import de.dasbabypixel.gamestages.common.data.server.PlayerStages;
import org.jspecify.annotations.NonNull;

public interface ServerPlayer extends Player {
    @Override
    @NonNull PlayerStages getGameStages();
}
