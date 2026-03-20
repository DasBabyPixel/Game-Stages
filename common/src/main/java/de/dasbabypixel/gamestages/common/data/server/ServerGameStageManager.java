package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.network.PacketConsumer;
import de.dasbabypixel.gamestages.common.network.Status;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static de.dasbabypixel.gamestages.common.CommonInstances.platformPacketCreator;

public class ServerGameStageManager extends MutatableGameStageManager {
    public static @Nullable ServerGameStageManager INSTANCE;
    private static boolean queuing = false;
    private final StagesFileProvider stagesFileProvider;

    private ServerGameStageManager(Path dataDirectory) {
        this.stagesFileProvider = new StagesFileProvider(dataDirectory.resolve("unlocked"));
    }

    public static void stop() {
        Objects.requireNonNull(INSTANCE).disallowMutation();
        try {
            INSTANCE.stagesFileProvider.shutdown();
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

    public static @NonNull MutatableGameStageManager instance() {
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

    public StagesFileProvider stagesFileProvider() {
        return stagesFileProvider;
    }

    public void sync(@NonNull PacketConsumer packetConsumer) {
        packetConsumer.send(platformPacketCreator.createStatusPacket(Status.BEGIN_SYNC));
        var gameStages = List.copyOf(this.gameStages());
        packetConsumer.send(platformPacketCreator.createSyncRegisteredGameStages(gameStages));
        for (var restriction : restrictions()) {
            packetConsumer.send(restriction.createPacket(this));
        }
        packetConsumer.send(platformPacketCreator.createStatusPacket(Status.END_SYNC));
    }
}
