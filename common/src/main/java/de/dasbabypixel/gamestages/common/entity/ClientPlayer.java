package de.dasbabypixel.gamestages.common.entity;

import de.dasbabypixel.gamestages.common.client.ClientPlayerStages;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ClientPlayer extends Player {
    @Override
    default ClientPlayerStages getGameStages() {
        throw new UnsupportedOperationException("Missing mixin override");
    }
}
