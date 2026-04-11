package de.dasbabypixel.gamestages.common.network;

import de.dasbabypixel.gamestages.common.data.GameStage;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface PlatformPacketCreator {
    CustomPacket createSyncRegisteredGameStages(List<GameStage> gameStages);

    CustomPacket createSyncUnlockedGameStages(List<GameStage> gameStages);

    CustomPacket createStatusPacket(Status status);
}
