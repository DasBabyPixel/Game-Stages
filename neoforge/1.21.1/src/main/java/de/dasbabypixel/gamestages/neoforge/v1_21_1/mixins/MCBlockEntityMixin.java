package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.Attachments;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.entity.IBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
@Mixin(BlockEntity.class)
@Implements(@Interface(iface = IBlockEntity.class, prefix = "stages$"))
public abstract class MCBlockEntityMixin implements IBlockEntity {

    @Shadow
    protected Level level;
    @Unique
    private boolean stages$registered = false;
    @Unique
    private @Nullable Set<UUID> stages$owners = null;

    @Inject(method = "clearRemoved", at = @At("TAIL"))
    private void onLoad(CallbackInfo ci) {
        if (level == null || level.isClientSide) return;
        if (stages$registered) return;

        var ah = (AttachmentHolder) (Object) this;
        var source = ah.getData(Attachments.SOURCE);
        stages$owners = Set.copyOf(source.owners());
        if (!stages$owners.isEmpty()) {
            Objects.requireNonNull(ServerGameStageManager.INSTANCE).playerStagesCache().requireComposite(stages$owners);
        }

        stages$registered = true;
    }

    public void stages$reloadOwners() {
        if (!stages$registered) throw new IllegalStateException();
        var ah = (AttachmentHolder) (Object) this;
        var source = ah.getData(Attachments.SOURCE);
        var newOwners = Set.copyOf(source.owners());
        if (newOwners.equals(stages$owners)) return;
        if (!stages$owners.isEmpty()) {
            Objects.requireNonNull(ServerGameStageManager.INSTANCE).playerStagesCache().releaseComposite(stages$owners);
        }
        stages$owners = newOwners;
        if (!stages$owners.isEmpty()) {
            Objects.requireNonNull(ServerGameStageManager.INSTANCE).playerStagesCache().requireComposite(stages$owners);
        }
    }

    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void onRemoved(CallbackInfo ci) {
        if (level == null || level.isClientSide) return;
        if (!stages$registered) return;

        if (!stages$owners.isEmpty()) {
            Objects.requireNonNull(ServerGameStageManager.INSTANCE).playerStagesCache().releaseComposite(stages$owners);
        }

        stages$registered = false;
    }

//    @Inject(method = "onChunkUnloaded", at = @At("HEAD"))
//    private void teammod$onChunkUnload(CallbackInfo ci) {
//        if (level == null || level.isClientSide) return;
//        if (!registered) return;
//
//        UUID teamId = getTeamId();
//        if (teamId == null) return;
//
//        TeamManager.release(teamId);
//        registered = false;
//    }
}
