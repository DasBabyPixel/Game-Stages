package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import com.mojang.authlib.GameProfile;
import de.dasbabypixel.gamestages.common.data.server.GlobalServerState;
import de.dasbabypixel.gamestages.common.data.server.PlayerStages;
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

@SuppressWarnings("DataFlowIssue")
@Mixin(ServerPlayer.class)
@Implements(@Interface(iface = de.dasbabypixel.gamestages.common.entity.ServerPlayer.class, prefix = "stages$"))
@NullMarked
public abstract class MCServerPlayerMixin implements de.dasbabypixel.gamestages.common.entity.ServerPlayer {
    @Unique
    private final PlayerStages game_Stages$playerStages = GlobalServerState
            .state()
            .stagesCache()
            .requirePlayer(getUniqueId());

    @Inject(method = "<init>", at = @At("TAIL"))
    public void lazyInit(MinecraftServer server, ServerLevel level, GameProfile gameProfile, ClientInformation clientInformation, CallbackInfo ci) {
        C.lazyInit(game_Stages$playerStages, (ServerPlayer) (Object) this, server);
    }

    public PlayerStages stages$getGameStages() {
        return game_Stages$playerStages;
    }

    // hack to ensure mixins don't crash immediately without FTBTeams present
    public static class C {
        public static void lazyInit(PlayerStages game_Stages$playerStages, ServerPlayer player, MinecraftServer server) {
            if (Mods.FTB_TEAMS.isLoaded()) {
                Runnable r = () -> {
                    var api = Objects.requireNonNull(FTBTeamsAPI.api());
                    var manager = Objects.requireNonNull(api.getManager());
                    var team = Objects.requireNonNull(manager.getTeamForPlayer(player));
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
    }
}
