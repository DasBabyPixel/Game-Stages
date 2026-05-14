package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.item;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.VItemAddon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@NullMarked
@Mixin(AbstractFurnaceBlockEntity.class)
public class MCAbstractFurnaceBlockEntityMixin {
    @Inject(method = "getBurnDuration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getBurnTime(Lnet/minecraft/world/item/crafting/RecipeType;)I"), cancellable = true)
    private void canBurn(ItemStack fuel, CallbackInfoReturnable<Integer> cir) {
        var blockEntity = (AbstractFurnaceBlockEntity) (Object) this;
        Objects.requireNonNull(blockEntity);
        var stages = blockEntity.stages();
        if (stages == null) return;
        var stack = blockEntity.getItem(1); // Fuel slot
        var entry = VItemAddon.getEntry(stages, stack, stack);
        if (entry == null) return;
        if (entry.predicate().test()) return;
        cir.setReturnValue(0);
        cir.cancel();
    }
}
