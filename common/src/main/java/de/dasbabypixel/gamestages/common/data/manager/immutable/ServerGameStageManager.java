package de.dasbabypixel.gamestages.common.data.manager.immutable;

import de.dasbabypixel.gamestages.common.addon.Addon.NetworkSyncConfigEvent;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.network.PacketConsumer;
import de.dasbabypixel.gamestages.common.network.Status;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;

import static de.dasbabypixel.gamestages.common.CommonInstances.platformPacketCreator;
import static de.dasbabypixel.gamestages.common.addon.Addon.NETWORK_SYNC_CONFIG_EVENT;

@NullMarked
public final class ServerGameStageManager extends AbstractGameStageManager<ServerGameStageManager> {

    public ServerGameStageManager(Collection<? extends GameStage> gameStages, Collection<? extends RestrictionEntry.PreCompiled<?, ?>> restrictions) {
        super(gameStages, restrictions);
    }

    public void sync(PacketConsumer packetConsumer) {
        packetConsumer.send(platformPacketCreator.createStatusPacket(Status.BEGIN_SYNC));
        var gameStages = List.copyOf(this.gameStages());
        packetConsumer.send(platformPacketCreator.createSyncRegisteredGameStages(gameStages));
        for (var restriction : restrictions()) {
            packetConsumer.send(restriction.createPacket(this));
        }
        NETWORK_SYNC_CONFIG_EVENT.call(new NetworkSyncConfigEvent(this, packetConsumer));
        packetConsumer.send(platformPacketCreator.createStatusPacket(Status.END_SYNC));
    }
}
