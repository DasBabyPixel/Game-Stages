package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class TeamStages extends ServerStages {
    private final Set<UUID> players = new HashSet<>();

    public TeamStages(ServerGameStageManager manager, StagesFileProvider.Key key, StagesFileProvider.TeamStagesFile stagesFile) {
        super(manager, key, stagesFile);
        this.players.addAll(stagesFile.players());
    }

    @Override
    protected StagesFileProvider.StagesFile createFile() {
        return new StagesFileProvider.TeamStagesFile(getUnlockedStages(), players());
    }

    public Set<UUID> players() {
        return players;
    }

    @Override
    protected Collection<? extends ServerPlayer> onlinePlayers() {
        var set = new HashSet<ServerPlayer>();
        for (var playerId : players) {
            var player = CommonInstances.platformPlayerProvider.getPlayer(playerId);
            if (player != null) set.add(player);
        }
        return set;
    }
}
