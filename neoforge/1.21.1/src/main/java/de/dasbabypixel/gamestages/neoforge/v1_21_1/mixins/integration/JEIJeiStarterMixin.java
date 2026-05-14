package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.integration;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei.JEIIntegration;
import mezz.jei.library.startup.JeiStarter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JeiStarter.class, remap = false)
public class JEIJeiStarterMixin {
    @Inject(method = "start", at = @At("HEAD"))
    private void start(CallbackInfo ci) {
        JEIIntegration.isReloading = true;
    }

    @Inject(method = "start", at = @At("TAIL"))
    private void done(CallbackInfo ci) {
        JEIIntegration.isReloading = false;
    }
}
