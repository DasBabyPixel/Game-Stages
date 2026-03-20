package de.dasbabypixel.gamestages.common.entity;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public interface Player {
    @NonNull BaseStages getGameStages();

    @NonNull UUID getUniqueId();
}
