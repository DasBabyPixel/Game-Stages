package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.ReloadHandler;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@NullMarked
@Mixin(PlayerList.class)
public class MCPlayerListMixin {
    // Send stages before recipes are sent
    @Inject(method = "placeNewPlayer", at = @At(value = "NEW", target = "(Ljava/util/Collection;)Lnet/minecraft/network/protocol/game/ClientboundUpdateRecipesPacket;"))
    private void syncStages(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        ReloadHandler.initializeNewPlayer(player);
    }
}
