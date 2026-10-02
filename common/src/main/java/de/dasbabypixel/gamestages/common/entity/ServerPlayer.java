package de.dasbabypixel.gamestages.common.entity;

import de.dasbabypixel.gamestages.common.data.server.PlayerStages;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ServerPlayer extends Player {
    @Override
    default PlayerStages getGameStages() {
        throw new UnsupportedOperationException("Missing mixin override");
    }
}
