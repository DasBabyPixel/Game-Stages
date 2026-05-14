package de.dasbabypixel.gamestages.common.data.manager.immutable;

import de.dasbabypixel.gamestages.common.addon.Addon.NetworkSyncConfigEvent;
import de.dasbabypixel.gamestages.common.data.attribute.AttributeEntry;
import de.dasbabypixel.gamestages.common.network.PacketConsumer;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static de.dasbabypixel.gamestages.common.CommonInstances.platformPacketCreator;
import static de.dasbabypixel.gamestages.common.addon.Addon.NETWORK_SYNC_CONFIG_EVENT;

@NullMarked
public final class ServerGameStageManager extends AbstractGameStageManager<ServerGameStageManager> {

    public ServerGameStageManager(Collection<AttributeEntry<? super ServerGameStageManager, ?>> attributes) {
        super(attributes);
    }

    public void sync(PacketConsumer packetConsumer) {
        packetConsumer.send(platformPacketCreator.createBeginSyncPacket(get(VERSION)));
        var gameStages = Objects.requireNonNull(List.copyOf(this.gameStages()));
        packetConsumer.send(platformPacketCreator.createSyncRegisteredGameStages(gameStages));
        for (var restriction : restrictions()) {
            packetConsumer.send(restriction.createPacket(this));
        }
        NETWORK_SYNC_CONFIG_EVENT.call(new NetworkSyncConfigEvent(this, packetConsumer));
        packetConsumer.send(platformPacketCreator.createEndSyncPacket());
    }
}
