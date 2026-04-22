package de.dasbabypixel.gamestages.common.data.manager.immutable;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

@NullMarked
public final class ClientGameStageManager extends AbstractGameStageManager<ClientGameStageManager> {
    private static @Nullable ClientGameStageManager currentManager;

    public ClientGameStageManager(Collection<? extends GameStage> gameStages, Collection<? extends RestrictionEntry.PreCompiled<?, ?>> restrictions) {
        super(gameStages, restrictions);
    }

    public static ClientGameStageManager currentManager() {
        return Objects.requireNonNull(currentManager);
    }

    public static boolean initialized() {
        return currentManager != null;
    }

    public static void update(ClientGameStageManager manager) {
        currentManager = Objects.requireNonNull(manager);
    }
}
