package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.network.PacketConsumer;
import de.dasbabypixel.gamestages.common.network.Status;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static de.dasbabypixel.gamestages.common.CommonInstances.platformPacketCreator;

public class ServerGameStageManager extends MutatableGameStageManager {
    public static @Nullable ServerGameStageManager INSTANCE;
    private final Map<Attribute<?>, Object> attributeMap = new HashMap<>();
//    private static boolean queuing = false;

    @SuppressWarnings("unchecked")
    public <T> T get(Attribute<? extends T> attribute) {
        return (T) attributeMap.computeIfAbsent(attribute, a -> a.defaultValue().apply(this));
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

    public record Attribute<T>(Function<@NonNull ServerGameStageManager, ? extends @NonNull T> defaultValue) {
        public Attribute(Supplier<? extends @NonNull T> defaultValue) {
            this(ignore -> defaultValue.get());
        }
    }

    @Override
    public void reset() {
        super.reset();
        attributeMap.clear();
    }

    public static void stop() {
        Objects.requireNonNull(INSTANCE).disallowMutation();
        INSTANCE = null;
    }

    public static void init() {
        if (INSTANCE != null) throw new IllegalStateException("Instance not null");
        INSTANCE = new ServerGameStageManager();
        INSTANCE.disallowMutation();
//        if (queuing) {
//            QueuingGameStageManager.INSTANCE.end(INSTANCE);
//            queuing = false;
//        }
    }

    public static @NonNull MutatableGameStageManager instance() {
        if (INSTANCE != null) {
//            if (queuing) {
//                QueuingGameStageManager.INSTANCE.end(INSTANCE);
//                queuing = false;
//            }
            return INSTANCE;
        }
        throw new UnsupportedOperationException();
//        if (!queuing) {
//            queuing = true;
//            QueuingGameStageManager.INSTANCE.begin();
//        }
//        return QueuingGameStageManager.INSTANCE;
    }
}
