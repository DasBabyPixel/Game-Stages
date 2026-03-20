package de.dasbabypixel.gamestages.common.entity;

import de.dasbabypixel.gamestages.common.client.ClientPlayerStages;
import org.jspecify.annotations.NonNull;

public interface ClientPlayer extends Player {
    @Override
    @NonNull ClientPlayerStages getGameStages();
}
