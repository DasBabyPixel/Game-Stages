package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

@NullMarked
public class GlobalServerState {
    private static @Nullable GlobalServerState state = null;
    private static @Nullable ServerGameStageManager currentManager;
    private final StagesFileProvider stagesFileProvider;
    private final StagesCache stagesCache;

    private GlobalServerState(Path dataDirectory) {
        stagesFileProvider = new StagesFileProvider(dataDirectory);
        stagesCache = new StagesCache(this);
    }

    public StagesFileProvider stagesFileProvider() {
        return stagesFileProvider;
    }

    public StagesCache stagesCache() {
        return stagesCache;
    }

    public static void updateManager(ServerGameStageManager manager) {
        currentManager = Objects.requireNonNull(manager);
    }

    public static ServerGameStageManager currentManager() {
        return Objects.requireNonNull(currentManager);
    }

    public static GlobalServerState state() {
        return Objects.requireNonNull(state);
    }

    public static boolean initialized() {
        return state != null;
    }

    public static void init(Path dataDirectory) {
        if (state != null) throw new IllegalStateException();
        if (currentManager == null) throw new IllegalStateException();
        state = new GlobalServerState(dataDirectory);
    }

    public static void stop() {
        if (state == null) throw new IllegalStateException();
        try {
            state.stagesCache.shutdown();
            state.stagesFileProvider.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        state = null;
    }
}