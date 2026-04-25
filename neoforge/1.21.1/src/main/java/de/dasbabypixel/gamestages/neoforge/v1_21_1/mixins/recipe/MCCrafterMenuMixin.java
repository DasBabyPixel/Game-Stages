package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.sugar.Local;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CrafterBlock;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@NullMarked
@Mixin(CrafterMenu.class)
public class MCCrafterMenuMixin {
    @Shadow
    @Final
    private ResultContainer resultContainer;

    @Inject(method = "refreshRecipeResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CrafterBlock;getPotentialResults(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/CraftingInput;)Ljava/util/Optional;"), cancellable = true)
    private void stages$refreshRecipeResult(CallbackInfo ci, @Local ServerPlayer serverPlayer, @Local Level level, @Local CraftingInput craftingInput) {
        var itemStack = CrafterBlock.getPotentialResults(level, craftingInput).map(holder -> {
            var recipe = holder.value();
            var entry = VRecipeAddon.getEntry(serverPlayer.getGameStages(), holder);
            if (entry != null) {
                if (!entry.predicate().test()) {
                    return ItemStack.EMPTY;
                }
            }
            return recipe.assemble(craftingInput, level.registryAccess());
        }).orElse(ItemStack.EMPTY);

        resultContainer.setItem(0, itemStack);

        ci.cancel();
    }
}
