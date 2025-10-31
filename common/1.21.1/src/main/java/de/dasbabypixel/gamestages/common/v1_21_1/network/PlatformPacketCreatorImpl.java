package de.dasbabypixel.gamestages.common.v1_21_1.network;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.network.PlatformPacketCreator;
import de.dasbabypixel.gamestages.common.network.Status;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.StatusPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.SyncRegisteredGameStagesPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.SyncUnlockedGameStagesPacket;

import java.util.List;

public class PlatformPacketCreatorImpl implements PlatformPacketCreator {
    @Override
    public CustomPacket createSyncRegisteredGameStages(List<GameStage> gameStages) {
        return new SyncRegisteredGameStagesPacket(gameStages);
    }

    @Override
    public CustomPacket createSyncUnlockedGameStages(List<GameStage> gameStages) {
        return new SyncUnlockedGameStagesPacket(gameStages);
    }

    @Override
    public CustomPacket createStatusPacket(Status status) {
        return new StatusPacket(status);
    }
}
