package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.ReloadHandler;
import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@NullMarked
@Mixin(MinecraftServer.class)
public class MCMinecraftServerMixin {
    @Inject(method = "reloadResources", at = @At("RETURN"))
    private void reload(Collection<String> selectedIds, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        var future = cir.getReturnValue();
        var server = (MinecraftServer) (Object) this;
        future.thenRunAsync(() -> {
            var resources = Objects.requireNonNull(server).getServerResources().managers();
            var registryAccess = server.registryAccess();
            ReloadHandler.fullReload(resources, registryAccess);
        }, server);
    }
}
