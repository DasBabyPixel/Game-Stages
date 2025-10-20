package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import net.minecraft.world.entity.player.Player;

public class StageAddedToPlayerEventJS implements KubePlayerEvent {
    private final Player player;
    private final String stage;

    public StageAddedToPlayerEventJS(Player player, String stage) {
        this.player = player;
        this.stage = stage;
    }

    public String getStage() {
        return stage;
    }

    @Override
    public Player getEntity() {
        return player;
    }
}
