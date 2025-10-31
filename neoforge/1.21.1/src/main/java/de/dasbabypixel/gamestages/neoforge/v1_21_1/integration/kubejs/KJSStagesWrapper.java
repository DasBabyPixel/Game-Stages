package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.server.PlayerStages;
import dev.latvian.mods.kubejs.stages.Stages;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class KJSStagesWrapper implements Stages {
    private final Player player;
    private final PlayerStages stages;

    public KJSStagesWrapper(Player player) {
        this.player = player;
        this.stages = ((de.dasbabypixel.gamestages.common.entity.Player) player).getGameStages();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean addNoUpdate(String s) {
        return stages.addSilent(new GameStage(s));
    }

    @Override
    public boolean removeNoUpdate(String s) {
        return stages.removeSilent(new GameStage(s));
    }

    @Override
    public boolean add(String stage) {
        return stages.add(new GameStage(stage));
    }

    @Override
    public boolean remove(String stage) {
        return stages.remove(new GameStage(stage));
    }

    @Override
    public Collection<String> getAll() {
        return stages.getAll().stream().map(GameStage::name).toList();
    }

    @Override
    public void sync() {
        stages.fullSync();
    }
}
