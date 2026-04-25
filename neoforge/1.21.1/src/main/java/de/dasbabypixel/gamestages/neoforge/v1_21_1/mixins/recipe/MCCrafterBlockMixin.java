package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.sugar.Local;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.entity.IBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@NullMarked
@Mixin(CrafterBlock.class)
public class MCCrafterBlockMixin {
    @Inject(method = "dispenseFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeHolder;value()Lnet/minecraft/world/item/crafting/Recipe;"), cancellable = true)
    private void stages$dispense(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci, @Local CrafterBlockEntity crafter, @Local RecipeHolder<CraftingRecipe> recipe) {
        @SuppressWarnings("RedundantCast") var stages = ((IBlockEntity) crafter).stages();
        if (stages == null) return;
        var entry = VRecipeAddon.getEntry(stages, recipe);
        if (entry != null) {
            if (!entry.predicate().test()) {
                level.levelEvent(1050, pos, 0);
                ci.cancel();
            }
        }
    }
}
