package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.data.server.ServerPlayerStages;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
@Implements(@Interface(iface = de.dasbabypixel.gamestages.common.entity.ServerPlayer.class, prefix = "stages$"))
public abstract class MCServerPlayerMixin implements de.dasbabypixel.gamestages.common.entity.ServerPlayer {
    @Unique
    private final ServerPlayerStages game_Stages$playerStages = new ServerPlayerStages((ServerGameStageManager) ServerGameStageManager.instance(), this);

    public @NonNull ServerPlayerStages stages$getGameStages() {
        return game_Stages$playerStages;
    }
}
