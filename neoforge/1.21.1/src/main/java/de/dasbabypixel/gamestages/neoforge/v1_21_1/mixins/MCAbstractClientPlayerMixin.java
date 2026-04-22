package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import de.dasbabypixel.gamestages.common.client.ClientPlayerStages;
import de.dasbabypixel.gamestages.common.entity.ClientPlayer;
import net.minecraft.client.player.AbstractClientPlayer;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@Mixin(AbstractClientPlayer.class)
@Implements(@Interface(iface = ClientPlayer.class, prefix = "stages$"))
@NullMarked
public abstract class MCAbstractClientPlayerMixin implements ClientPlayer {
    @Unique
    private final ClientPlayerStages game_Stages$playerStages = new ClientPlayerStages();

    public ClientPlayerStages stages$getGameStages() {
        return Objects.requireNonNull(game_Stages$playerStages);
    }
}
