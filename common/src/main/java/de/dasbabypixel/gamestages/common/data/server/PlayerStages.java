package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@NullMarked
public class PlayerStages extends ServerStages {
    private static final Logger LOGGER = Objects.requireNonNull(Logger.getLogger(PlayerStages.class.getName()));
    private final List<ServerPlayer> playerList = new ArrayList<>(1);
    private final StagesCache stagesCache;
    private boolean valid = true;
    private @Nullable UUID teamId;
    private @Nullable TeamStages team;

    public PlayerStages(StagesFileProvider stagesFileProvider, StagesCache stagesCache, StagesFileProvider.Key key, StagesFileProvider.PlayerStagesFile stagesFile) {
        super(stagesFileProvider, key, stagesFile);
        this.stagesCache = stagesCache;
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
        LOGGER.log(Level.WARNING, "Updating wrongly saved team by external API");
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
    public Set<GameStage> getUnlockedStages() {
        if (!valid) throw new IllegalStateException();
        return super.getUnlockedStages();
    }

    public void setTeam(@Nullable UUID teamId) {
        if (!valid) throw new IllegalStateException();
        this.teamId = teamId;
        if (teamId == null) {
            if (team != null) {
                stagesCache.release(team);
                team = null;
                writeFile();
            }
        } else {
            if (team == null) {
                team = stagesCache.requireTeam(teamId);
                writeFile();
            } else if (!team.key().uuid().equals(teamId)) {
                stagesCache.release(team);
                team = stagesCache.requireTeam(teamId);
                writeFile();
            }
        }
    }

    @Override
    public void unload() {
        super.unload();
        if (team != null) {
            stagesCache.release(team);
            team = null;
        }
        valid = false;
    }

    public ServerStages get() {
        if (!valid) throw new IllegalStateException();
        return team == null ? this : team;
    }
}
