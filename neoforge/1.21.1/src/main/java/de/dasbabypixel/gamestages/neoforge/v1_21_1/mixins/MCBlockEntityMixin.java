package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import de.dasbabypixel.gamestages.common.data.server.CompositeStages;
import de.dasbabypixel.gamestages.common.data.server.GlobalServerState;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.Attachments;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.entity.IBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
@Mixin(BlockEntity.class)
@Implements(@Interface(iface = IBlockEntity.class, prefix = "stages$"))
@NullMarked
public abstract class MCBlockEntityMixin implements IBlockEntity {
    @Shadow
    protected @Nullable Level level;
    @Unique
    private boolean stages$registered = false;
    @Unique
    private @Nullable Set<UUID> stages$owners = null;
    @Unique
    private @Nullable CompositeStages stages$stages;

    @Inject(method = "clearRemoved", at = @At("TAIL"))
    private void onLoad(CallbackInfo ci) {
        if (level == null || level.isClientSide) return;
        if (stages$registered) return;

        var ah = (AttachmentHolder) (Object) this;
        var source = ah.getData(Attachments.SOURCE);
        stages$owners = Set.copyOf(source.owners());
        if (!stages$owners.isEmpty()) {
            stages$stages = GlobalServerState.state().stagesCache().requireComposite(stages$owners);
        }

        stages$registered = true;
    }

    public @Nullable CompositeStages stages$stages() {
        return stages$stages;
    }

    public void stages$reloadOwners() {
        if (!stages$registered) throw new IllegalStateException();
        var ah = (AttachmentHolder) (Object) this;
        var source = ah.getData(Attachments.SOURCE);
        var newOwners = Set.copyOf(source.owners());
        if (newOwners.equals(stages$owners)) return;
        if (!stages$owners.isEmpty()) {
            GlobalServerState.state().stagesCache().releaseComposite(stages$owners);
            stages$stages = null;
        }
        stages$owners = newOwners;
        if (!stages$owners.isEmpty()) {
            stages$stages = GlobalServerState.state().stagesCache().requireComposite(stages$owners);
        }
    }

    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void onRemoved(CallbackInfo ci) {
        if (level == null || level.isClientSide) return;
        if (!stages$registered) return;

        if (!stages$owners.isEmpty()) {
            GlobalServerState.state().stagesCache().releaseComposite(stages$owners);
            stages$stages = null;
        }

        stages$registered = false;
    }
}
