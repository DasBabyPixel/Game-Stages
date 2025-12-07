package de.dasbabypixel.gamestages.neoforge.v1_21_1.data;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.PlatformPlayerStagesProvider;
import de.dasbabypixel.gamestages.common.entity.Player;

import java.util.List;
import java.util.Set;

public class PlatformPlayerStagesProviderImpl implements PlatformPlayerStagesProvider {
    @Override
    public void setStages(Player player_, Set<GameStage> unlockedStages) {
        net.minecraft.world.entity.player.Player player = (net.minecraft.world.entity.player.Player) player_;
        player.setData(Attachments.ATTACHMENT_REFERENCES, List.copyOf(unlockedStages));
        System.out.println(unlockedStages);
        System.out.println(player.getData(Attachments.ATTACHMENT_REFERENCES));
    }

    @Override
    public Set<GameStage> getStages(Player player_) {
        net.minecraft.world.entity.player.Player player = (net.minecraft.world.entity.player.Player) player_;
        System.out.println(player.getData(Attachments.ATTACHMENT_REFERENCES));
        var stages = player.getData(Attachments.ATTACHMENT_REFERENCES);
        return Set.copyOf(stages);
    }
}
