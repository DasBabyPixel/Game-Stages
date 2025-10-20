package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import de.dasbabypixel.gamestages.common.data.server.PlayerStages;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
@Implements(@Interface(iface = de.dasbabypixel.gamestages.common.entity.Player.class, prefix = "gamestages$"))
public abstract class MCPlayerMixin {
    @Unique
    private final PlayerStages game_Stages$playerStages = new PlayerStages((de.dasbabypixel.gamestages.common.entity.Player) this);

    public PlayerStages gamestages$getGameStages() {
        return game_Stages$playerStages;
    }
}
