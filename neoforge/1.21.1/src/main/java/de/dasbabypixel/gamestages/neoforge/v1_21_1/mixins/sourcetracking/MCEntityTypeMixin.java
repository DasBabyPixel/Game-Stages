package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.sourcetracking;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.Attachments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@NullMarked
@Mixin(EntityType.class)
public class MCEntityTypeMixin {
    @Inject(method = "spawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/MobSpawnType;ZZ)Lnet/minecraft/world/entity/Entity;", at = @At("RETURN"))
    private void spawn(ServerLevel serverLevel, @Nullable ItemStack stack, @Nullable Player player, BlockPos pos, MobSpawnType spawnType, boolean shouldOffsetY, boolean shouldOffsetYMore, CallbackInfoReturnable<Entity> cir) {
        if (player != null) {
            var source = player.getData(Attachments.SOURCE);
            var owners = source.owners();
            if (owners.isEmpty()) return;
            var entity = cir.getReturnValue();
            var entitySource = entity.getData(Attachments.SOURCE);
            entitySource.setOwners(owners);
        }
    }
}
