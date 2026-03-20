package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerStages extends ServerStages {
    private final List<ServerPlayer> playerList = new ArrayList<>(1);
    private @Nullable TeamStages team;

    public PlayerStages(@NonNull ServerGameStageManager manager, StagesFileProvider.@NonNull Key key, StagesFileProvider.PlayerStagesFile stagesFile) {
        super(manager, key, stagesFile);
        setTeam(stagesFile.teamId());
    }

    @Override
    protected StagesFileProvider.StagesFile createFile() {
        return new StagesFileProvider.PlayerStagesFile(getUnlockedStages(), team == null ? null : team.key().uuid());
    }

    @Override
    protected Collection<? extends ServerPlayer> onlinePlayers() {
        var player = CommonInstances.platformPlayerProvider.getPlayer(this.key.uuid());
        if (player == null) return List.of();
        playerList.clear();
        playerList.add(player);
        return playerList;
    }

    public void setTeam(@Nullable UUID teamId) {
        if (teamId == null) {
            if (team != null) {
                manager.playerStagesCache().release(team);
                team = null;
            }
        } else {
            if (team == null) {
                team = manager.playerStagesCache().requireTeam(teamId);
            } else if (!team.key().uuid().equals(teamId)) {
                manager.playerStagesCache().release(team);
                team = manager.playerStagesCache().requireTeam(teamId);
            }
        }
    }

    public void unload() {
        setTeam(null);
    }

    public ServerStages get() {
        return team == null ? this : team;
    }
}
