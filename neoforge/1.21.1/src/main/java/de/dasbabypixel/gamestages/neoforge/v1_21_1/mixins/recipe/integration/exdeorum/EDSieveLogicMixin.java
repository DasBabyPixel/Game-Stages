package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe.integration.exdeorum;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.entity.IBlockEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thedarkcolour.exdeorum.blockentity.logic.SieveLogic;
import thedarkcolour.exdeorum.recipe.sieve.SieveRecipe;

import java.util.List;

@NullMarked
@Mixin(SieveLogic.class)
public class EDSieveLogicMixin {
    @Shadow
    @Final
    private SieveLogic.Owner owner;

    @Inject(method = "getDropsFor", at = @At("RETURN"))
    private void getDropsFor(ItemStack contents, CallbackInfoReturnable<List<? extends SieveRecipe>> cir) {
        if (owner instanceof IBlockEntity blockEntity) {
            var stages = blockEntity.stages();
            if (stages != null) {
                var list = cir.getReturnValue();
            }
        }
    }
}
