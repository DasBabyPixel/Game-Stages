package de.dasbabypixel.gamestages.common.client;

import de.dasbabypixel.gamestages.common.addon.Addon.ClientPostSyncUnlockedStagesEvent;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static de.dasbabypixel.gamestages.common.addon.Addon.CLIENT_POST_SYNC_UNLOCKED_STAGES_EVENT;

@NullMarked
public class ClientPlayerStages extends BaseStages {
    private static final Logger LOGGER = Objects.requireNonNull(Logger.getLogger(ClientPlayerStages.class.getName()));

    public ClientPlayerStages() {
        super(Set.of());
        if (ClientGameStageManager.initialized()) {
            recompileAll(ClientGameStageManager.currentManager());
        }
    }

    public void syncUnlockedStages(List<GameStage> gameStages) {
        var time1 = System.nanoTime();
        var updated = new HashSet<GameStage>();
        var keep = Objects.requireNonNull(Set.copyOf(gameStages));
        var it = Objects.requireNonNull(getUnlockedStages().iterator());
        while (it.hasNext()) {
            var stage = it.next();
            if (keep.contains(stage)) continue;
            it.remove();
            updated.add(stage);
        }
        for (var stage : gameStages) {
            if (getUnlockedStages().add(stage)) {
                updated.add(stage);
            }
        }
        updated.forEach(this::update);
        var took = System.nanoTime() - time1;
        LOGGER.info("Syncing unlocked stages took " + TimeUnit.NANOSECONDS.toMillis(took) + "ms");
        // TODO do we need to test eagerly?
        CLIENT_POST_SYNC_UNLOCKED_STAGES_EVENT.call(new ClientPostSyncUnlockedStagesEvent(this));
    }
}
