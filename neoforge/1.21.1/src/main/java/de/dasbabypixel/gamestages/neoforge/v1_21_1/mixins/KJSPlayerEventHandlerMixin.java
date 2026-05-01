package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import dev.latvian.mods.kubejs.player.KubeJSPlayerEventHandler;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@NullMarked
@Mixin(KubeJSPlayerEventHandler.class)
public class KJSPlayerEventHandlerMixin {
    @Inject(method = "loggedIn", at = @At(value = "INVOKE", target = "Ldev/latvian/mods/kubejs/stages/Stages;sync()V"), cancellable = true)
    private static void loggedIn(PlayerEvent.PlayerLoggedInEvent event, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "respawn", at = @At(value = "INVOKE", target = "Ldev/latvian/mods/kubejs/stages/Stages;sync()V"), cancellable = true)
    private static void respawn(PlayerEvent.PlayerRespawnEvent event, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "dimensionChanged", at = @At(value = "INVOKE", target = "Ldev/latvian/mods/kubejs/stages/Stages;sync()V"), cancellable = true)
    private static void dimensionChanged(PlayerEvent.PlayerChangedDimensionEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
