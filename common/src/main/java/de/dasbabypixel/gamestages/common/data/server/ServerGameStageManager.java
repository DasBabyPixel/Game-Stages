package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.addon.Addon.NetworkSyncConfigEvent;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.network.PacketConsumer;
import de.dasbabypixel.gamestages.common.network.Status;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static de.dasbabypixel.gamestages.common.CommonInstances.platformPacketCreator;
import static de.dasbabypixel.gamestages.common.addon.Addon.NETWORK_SYNC_CONFIG_EVENT;

@NullMarked
public class ServerGameStageManager extends MutableGameStageManager {
    public static @Nullable ServerGameStageManager INSTANCE;
    private static boolean queuing = false;
    private final StagesFileProvider stagesFileProvider;
    private final StagesCache stagesCache;

    private ServerGameStageManager(Path dataDirectory) {
        this.stagesFileProvider = new StagesFileProvider(Objects.requireNonNull(dataDirectory.resolve("unlocked")));
        this.stagesCache = new StagesCache(this);
    }

    void initAttributeMap(Map<Attribute<? super MutableGameStageManager, ?>, Object> attributeMap) {
        this.attributeMap.putAll(attributeMap);
    }

    public StagesCache playerStagesCache() {
        return stagesCache;
    }

    public StagesFileProvider stagesFileProvider() {
        return stagesFileProvider;
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

    public static void stop() {
        Objects.requireNonNull(INSTANCE).disallowMutation();
        try {
            INSTANCE.stagesFileProvider.shutdown();
            INSTANCE.stagesCache.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        INSTANCE = null;
    }

    public static void init(Path dataDirectory) {
        if (INSTANCE != null) throw new IllegalStateException("Instance not null");
        INSTANCE = new ServerGameStageManager(dataDirectory);
        INSTANCE.disallowMutation();
        if (queuing) {
            QueuingGameStageManager.INSTANCE.end(INSTANCE);
            queuing = false;
        }
    }

    public static MutableGameStageManager instance() {
        if (INSTANCE != null) {
            if (queuing) {
                QueuingGameStageManager.INSTANCE.end(INSTANCE);
                queuing = false;
            }
            return INSTANCE;
        }
        if (!queuing) {
            queuing = true;
            QueuingGameStageManager.INSTANCE.begin();
        }
        return QueuingGameStageManager.INSTANCE;
    }
}
