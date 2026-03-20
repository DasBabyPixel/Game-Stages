package de.dasbabypixel.gamestages.common.entity;

import de.dasbabypixel.gamestages.common.data.server.ServerPlayerStages;
import org.jspecify.annotations.NonNull;

public interface ServerPlayer extends Player {
    @Override
    @NonNull ServerPlayerStages getGameStages();
}
