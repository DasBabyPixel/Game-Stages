package de.dasbabypixel.gamestages.common.v1_21_1.network;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.network.PlatformPacketCreator;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.SyncFullDataPacket;

import java.util.List;

public class PlatformPacketCreatorImpl implements PlatformPacketCreator {
    @Override
    public CustomPacket createSyncRegisteredGameStages(List<GameStage> gameStages) {
        return new SyncFullDataPacket(gameStages);
    }
}
