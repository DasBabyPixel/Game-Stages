package de.dasbabypixel.gamestages.common.v1_21_1.network;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.network.PlatformPacketCreator;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.BeginSyncPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.EndSyncPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.SyncRegisteredGameStagesPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.SyncUnlockedGameStagesPacket;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
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
    public CustomPacket createBeginSyncPacket(int version) {
        return new BeginSyncPacket(version);
    }

    @Override
    public CustomPacket createEndSyncPacket() {
        return new EndSyncPacket();
    }
}
