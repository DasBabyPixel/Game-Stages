package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.sugar.Local;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@NullMarked
@Mixin(CraftingMenu.class)
public class MCCraftingMenuMixin {
    @Inject(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeHolder;value()Lnet/minecraft/world/item/crafting/Recipe;"), cancellable = true)
    private static void stages$slotChanged(AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftSlots, ResultContainer resultSlots, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci, @Local ServerPlayer serverPlayer, @Local(name = "recipeholder") RecipeHolder<CraftingRecipe> recipeholder) {
        var entry = VRecipeAddon.getEntry(serverPlayer.getGameStages(), recipeholder);
        if (entry != null) {
            if (!entry.predicate().test()) {
                resultSlots.clearContent();
                ci.cancel();
            }
        }
    }
}
