package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.entity.Player;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ServerPlayerStages extends BaseStages {
    private final @NonNull ServerGameStageManager manager;
    private final @NonNull ServerPlayer player;

    public ServerPlayerStages(@NonNull ServerGameStageManager manager, @NonNull ServerPlayer player) {
        this.manager = manager;
        this.player = player;
    }

    @Override
    protected Set<GameStage> fetchUnlockedStages() {
        try {
            return manager.stagesFileProvider().readStages(StagesFileProvider.player(player.getUniqueId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onAdd(@NonNull GameStage gameStage, boolean silent) {
        super.onAdd(gameStage, silent);
        manager.stagesFileProvider().writeStages(StagesFileProvider.player(player.getUniqueId()), getUnlockedStages());
        if (!silent) fullSync();
    }

    @Override
    protected void onRemove(@NonNull GameStage gameStage, boolean silent) {
        super.onRemove(gameStage, silent);
        manager.stagesFileProvider().writeStages(StagesFileProvider.player(player.getUniqueId()), getUnlockedStages());
        if (!silent) fullSync();
    }

    public void fullSync() {
        CommonInstances.platformPacketDistributor.sendToPlayer(player, CommonInstances.platformPacketCreator.createSyncUnlockedGameStages(List.copyOf(getUnlockedStages())));
    }

    public @NonNull Player getPlayer() {
        return player;
    }
}
