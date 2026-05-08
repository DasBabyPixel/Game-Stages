package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.sugar.Local;
import de.dasbabypixel.gamestages.common.v1_21_1.data.StageRefreshableMenu;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeThreadLocal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.util.Optional;

@NullMarked
@Implements(@Interface(iface = StageRefreshableMenu.class, prefix = "stages$"))
@Mixin(CraftingMenu.class)
public class MCCraftingMenuMixin {
    @Final
    @Shadow
    private ContainerLevelAccess access;
    @Final
    @Shadow
    private Player player;
    @Final
    @Shadow
    private CraftingContainer craftSlots;
    @Final
    @Shadow
    private ResultContainer resultSlots;

    public void stages$refresh() {
        this.access.execute((p_344363_, p_344364_) -> slotChangedCraftingGrid((CraftingMenu) (Object) this, p_344363_, this.player, this.craftSlots, this.resultSlots, null));
    }

    @Shadow
    protected static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftSlots, ResultContainer resultSlots, @Nullable RecipeHolder<CraftingRecipe> recipe) {
        throw new UnsupportedOperationException();
    }

    @Redirect(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;"))
    private static <T extends Recipe<RecipeInput>, I extends RecipeInput> Optional<RecipeHolder<T>> getRecipeForRedirect(RecipeManager instance, RecipeType<T> recipeType, I input, Level level, RecipeHolder<T> lastRecipe, @Local(argsOnly = true) Player player) {
        var stages = player.getGameStages();
        var recipes = RecipeThreadLocal.get();
        recipes.stages(stages);
        try {
            return instance.getRecipeFor(recipeType, input, level, lastRecipe);
        } finally {
            recipes.clearStages();
        }
    }
}
