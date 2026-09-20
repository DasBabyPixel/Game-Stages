package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.dasbabypixel.gamestages.common.v1_21_1.data.StageRefreshableMenu;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeThreadLocal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@NullMarked
@Mixin(SmithingMenu.class)
@Implements(@Interface(iface = StageRefreshableMenu.class, prefix = "stages$"))
public abstract class MCSmithingMenuMixin extends ItemCombinerMenu {
    public MCSmithingMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @Shadow
    public abstract void createResult();

    public void stages$refresh() {
        createResult();
    }

    @WrapOperation(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/List;"))
    private <T extends Recipe<I>, I extends RecipeInput> List<RecipeHolder<T>> createResult(RecipeManager instance, RecipeType<T> recipeType, I input, Level level, Operation<List<RecipeHolder<T>>> original) {
        var recipes = RecipeThreadLocal.get();
        recipes.pushStages(player.getGameStages());
        try {
            return original.call(instance, recipeType, input, level);
        } finally {
            recipes.popStages();
        }
    }
}
