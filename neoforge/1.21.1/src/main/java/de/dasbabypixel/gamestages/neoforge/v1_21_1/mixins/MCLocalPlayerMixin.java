package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import de.dasbabypixel.gamestages.common.client.ClientPlayerStages;
import de.dasbabypixel.gamestages.common.entity.ClientPlayer;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.client.ClientReloadHandler;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;

@Mixin(LocalPlayer.class)
@Implements(@Interface(iface = ClientPlayer.class, prefix = "stages$"))
@NullMarked
public abstract class MCLocalPlayerMixin implements ClientPlayer {
    public ClientPlayerStages stages$getGameStages() {
        return Objects.requireNonNull(ClientReloadHandler.stages);
    }
}
