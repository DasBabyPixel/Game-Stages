package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.dasbabypixel.gamestages.common.v1_21_1.data.StageRefreshableMenu;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeThreadLocal;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@NullMarked
@Mixin(StonecutterMenu.class)
@Implements(@Interface(iface = StageRefreshableMenu.class, prefix = "stages$"))
public abstract class MCStonecutterMenuMixin {

    @Shadow
    @Final
    public Container container;
    @Shadow
    private ItemStack input;
    @SuppressWarnings("NotNullFieldNotInitialized")
    @Unique
    private Player stages_recipe$player;

    @Shadow
    protected abstract void setupRecipeList(Container container, ItemStack stack);

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
    private void init(int containerId, Inventory playerInventory, ContainerLevelAccess access, CallbackInfo ci) {
        stages_recipe$player = Objects.requireNonNull(playerInventory.player);
    }

    public void stages$refresh() {
        setupRecipeList(container, input);
    }

    @WrapOperation(method = "setupRecipeList", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/List;"))
    private <T extends Recipe<I>, I extends RecipeInput> List<RecipeHolder<T>> setupRecipeList(RecipeManager instance, RecipeType<T> recipeType, I input, Level level, Operation<List<RecipeHolder<T>>> original) {
        var recipes = RecipeThreadLocal.get();
        recipes.pushStages(stages_recipe$player.getGameStages());
        try {
            return original.call(instance, recipeType, input, level);
        } finally {
            recipes.popStages();
        }
    }

    @WrapOperation(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private <T extends Recipe<I>, I extends RecipeInput> Optional<RecipeHolder<T>> quickMoveStack(RecipeManager instance, RecipeType<T> recipeType, I input, Level level, Operation<Optional<RecipeHolder<T>>> original) {
        var recipes = RecipeThreadLocal.get();
        recipes.pushStages(stages_recipe$player.getGameStages());
        try {
            return original.call(instance, recipeType, input, level);
        } finally {
            recipes.popStages();
        }
    }
}
