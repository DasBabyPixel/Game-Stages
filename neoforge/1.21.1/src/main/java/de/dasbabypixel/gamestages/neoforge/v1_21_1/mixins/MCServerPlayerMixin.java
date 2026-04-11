package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import com.mojang.authlib.GameProfile;
import de.dasbabypixel.gamestages.common.data.server.PlayerStages;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ServerPlayer.class)
@Implements(@Interface(iface = de.dasbabypixel.gamestages.common.entity.ServerPlayer.class, prefix = "stages$"))
@NullMarked
public abstract class MCServerPlayerMixin implements de.dasbabypixel.gamestages.common.entity.ServerPlayer {
    @Unique
    private final PlayerStages game_Stages$playerStages = ((ServerGameStageManager) ServerGameStageManager.instance())
            .playerStagesCache()
            .requirePlayer(getUniqueId());

    @Inject(method = "<init>", at = @At("TAIL"))
    public void lazyInit(MinecraftServer server, ServerLevel level, GameProfile gameProfile, ClientInformation clientInformation, CallbackInfo ci) {
        if (Mods.FTB_TEAMS.isLoaded()) {
            Runnable r = () -> {
                var api = Objects.requireNonNull(FTBTeamsAPI.api());
                var manager = Objects.requireNonNull(api.getManager());
                var team = Objects.requireNonNull(manager.getTeamForPlayer((ServerPlayer) (Object) this));
                if (team.isPresent()) {
                    var id = team.get().getId();
                    game_Stages$playerStages.updateTeamByExternalAPI(id);
                } else {
                    game_Stages$playerStages.updateTeamByExternalAPI(null);
                }
            };
            if (!server.isSameThread()) throw new IllegalStateException();
            if (server.isSameThread()) {
                r.run();
            } else {
                server.execute(r);
            }
        }
    }

    public PlayerStages stages$getGameStages() {
        return game_Stages$playerStages;
    }
}
