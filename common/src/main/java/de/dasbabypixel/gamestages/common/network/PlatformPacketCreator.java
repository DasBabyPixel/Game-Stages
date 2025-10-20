package de.dasbabypixel.gamestages.common.network;

import de.dasbabypixel.gamestages.common.data.GameStage;

import java.util.List;

public interface PlatformPacketCreator {
    CustomPacket createSyncRegisteredGameStages(List<GameStage> gameStages);
}
