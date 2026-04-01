package de.dasbabypixel.gamestages.common.network;

import de.dasbabypixel.gamestages.common.data.GameStage;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface PlatformPacketCreator {
    @NonNull CustomPacket createSyncRegisteredGameStages(List<GameStage> gameStages);

    @NonNull CustomPacket createSyncUnlockedGameStages(List<GameStage> gameStages);

    @NonNull CustomPacket createStatusPacket(Status status);
}
