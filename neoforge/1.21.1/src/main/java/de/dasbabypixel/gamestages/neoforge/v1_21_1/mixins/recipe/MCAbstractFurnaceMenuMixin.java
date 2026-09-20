package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeThreadLocal;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.entity.IBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@NullMarked
@Mixin(AbstractFurnaceMenu.class)
public class MCAbstractFurnaceMenuMixin {
    @Shadow
    @Final
    private Container container;

    @WrapOperation(method = "canSmelt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    protected <T extends Recipe<I>, I extends RecipeInput> Optional<RecipeHolder<T>> canSmelt(RecipeManager instance, RecipeType<T> recipeType, I input, Level level, Operation<Optional<RecipeHolder<T>>> original) {
        var recipes = RecipeThreadLocal.get();
        if (level.isClientSide) {
            recipes.pushStages(null);
            try {
                return original.call(instance, recipeType, input, level);
            } finally {
                recipes.popStages();
            }
        } else {
            if (container instanceof IBlockEntity be) {
                recipes.pushStages(be.stages());
                try {
                    return original.call(instance, recipeType, input, level);
                } finally {
                    recipes.popStages();
                }
            }
            return original.call(instance, recipeType, input, level);
        }
    }
}
