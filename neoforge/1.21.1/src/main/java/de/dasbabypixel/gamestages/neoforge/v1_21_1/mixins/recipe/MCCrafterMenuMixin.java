package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import de.dasbabypixel.gamestages.common.v1_21_1.data.StageRefreshableMenu;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeThreadLocal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CrafterBlock;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@NullMarked
@Implements(@Interface(iface = StageRefreshableMenu.class, prefix = "stages$"))
@Mixin(CrafterMenu.class)
public abstract class MCCrafterMenuMixin implements StageRefreshableMenu {
    @Shadow
    @Final
    private Player player;

    @Shadow
    protected abstract void refreshRecipeResult();

    @Redirect(method = "refreshRecipeResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CrafterBlock;getPotentialResults(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/CraftingInput;)Ljava/util/Optional;"))
    private Optional<RecipeHolder<CraftingRecipe>> getPotentialResults(Level level, CraftingInput input) {
        var stages = player.getGameStages();
        var recipes = RecipeThreadLocal.get();
        recipes.stages(stages);
        try {
            return CrafterBlock.getPotentialResults(level, input);
        } finally {
            recipes.clearStages();
        }
    }

    public void stages$refresh() {
        refreshRecipeResult();
    }
}
