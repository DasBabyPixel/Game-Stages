package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.server.StagesFileProvider.Key;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

public abstract class ServerStages extends BaseStages {
    protected final @NonNull ServerGameStageManager manager;
    protected final @NonNull Key key;

    public ServerStages(@NonNull ServerGameStageManager manager, @NonNull Key key, StagesFileProvider.StagesFile stagesFile) {
        super(stagesFile.stages());
        this.manager = manager;
        this.key = key;
    }

    public @NonNull Key key() {
        return key;
    }

    @Override
    protected void onAdd(@NonNull GameStage gameStage, boolean silent) {
        super.onAdd(gameStage, silent);
        writeFile();
        if (!silent) fullSync();
    }

    @Override
    protected void onRemove(@NonNull GameStage gameStage, boolean silent) {
        super.onRemove(gameStage, silent);
        writeFile();
        if (!silent) fullSync();
    }

    protected void writeFile() {
        manager.stagesFileProvider().writeStages(key, createFile());
    }

    protected abstract StagesFileProvider.StagesFile createFile();

    protected abstract Collection<? extends ServerPlayer> onlinePlayers();

    public void fullSync() {
        var online = onlinePlayers();
        if (online.isEmpty()) return;
        var unlocked = List.copyOf(getUnlockedStages());
        for (var serverPlayer : online) {
            CommonInstances.platformPacketDistributor.sendToPlayer(serverPlayer, CommonInstances.platformPacketCreator.createSyncUnlockedGameStages(unlocked));
        }
    }
}
