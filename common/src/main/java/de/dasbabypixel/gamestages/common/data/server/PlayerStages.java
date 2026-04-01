package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class PlayerStages extends ServerStages {
    private final @NonNull List<ServerPlayer> playerList = new ArrayList<>(1);
    private boolean valid = true;
    private @Nullable UUID teamId;
    private @Nullable TeamStages team;

    public PlayerStages(@NonNull ServerGameStageManager manager, StagesFileProvider.@NonNull Key key, StagesFileProvider.@NonNull PlayerStagesFile stagesFile) {
        super(manager, key, stagesFile);
        this.teamId = stagesFile.teamId();
    }

    @Override
    public void load() {
        super.load();
        setTeam(teamId);
    }

    @Override
    protected StagesFileProvider.StagesFile createFile() {
        if (!valid) throw new IllegalStateException();
        return new StagesFileProvider.PlayerStagesFile(getUnlockedStages(), team == null ? null : team.key().uuid());
    }

    public void updateTeamByExternalAPI(@Nullable UUID newTeam) {
        if (team == null && newTeam == null) return;
        if (team != null && team.key().uuid().equals(newTeam)) return;
        System.out.println("Updating wrongly saved team by external API");
        setTeam(newTeam);
    }

    @Override
    protected Collection<? extends ServerPlayer> onlinePlayers() {
        if (!valid) throw new IllegalStateException();
        var player = CommonInstances.platformPlayerProvider.getPlayer(this.key.uuid());
        if (player == null) return List.of();
        playerList.clear();
        playerList.add(player);
        return playerList;
    }

    @Override
    protected @NonNull Set<@NonNull GameStage> getUnlockedStages() {
        if (!valid) throw new IllegalStateException();
        return super.getUnlockedStages();
    }

    public void setTeam(@Nullable UUID teamId) {
        if (!valid) throw new IllegalStateException();
        this.teamId = teamId;
        if (teamId == null) {
            if (team != null) {
                manager.playerStagesCache().release(team);
                team = null;
                writeFile();
            }
        } else {
            if (team == null) {
                team = manager.playerStagesCache().requireTeam(teamId);
                writeFile();
            } else if (!team.key().uuid().equals(teamId)) {
                manager.playerStagesCache().release(team);
                team = manager.playerStagesCache().requireTeam(teamId);
                writeFile();
            }
        }
    }

    @Override
    public void unload() {
        super.unload();
        if (team != null) {
            manager.playerStagesCache().release(team);
            team = null;
        }
        valid = false;
    }

    public ServerStages get() {
        if (!valid) throw new IllegalStateException();
        return team == null ? this : team;
    }
}
