package de.dasbabypixel.gamestages.common.entity;

import de.dasbabypixel.gamestages.common.data.PlayerStages;
import org.jspecify.annotations.NonNull;

public interface Player {
    @NonNull PlayerStages getGameStages();
}
