package de.dasbabypixel.gamestages.common.entity;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public interface Player {
    BaseStages getGameStages();

    UUID getUniqueId();
}
