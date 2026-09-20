package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeThreadLocal;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.entity.IBlockEntity;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@NullMarked
@Mixin(CampfireBlockEntity.class)
public abstract class MCCampfireBlockEntityMixin {
    @SuppressWarnings("UnnecessaryLocalVariable")
    @WrapOperation(method = "cookTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private static <T extends Recipe<I>, I extends RecipeInput> Optional<RecipeHolder<T>> cookTick(RecipeManager.CachedCheck<?, ?> instance, I i, Level level, Operation<Optional<RecipeHolder<T>>> original, @Local(argsOnly = true) CampfireBlockEntity blockEntity) {
        IBlockEntity be = blockEntity;
        var recipes = RecipeThreadLocal.get();
        recipes.pushStages(be.stages());
        try {
            return original.call(instance, i, level);
        } finally {
            recipes.popStages();
        }
    }

    @WrapOperation(method = "getCookableRecipe", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private <T extends Recipe<I>, I extends RecipeInput> Optional<RecipeHolder<T>> get(RecipeManager.CachedCheck<?, ?> instance, I i, Level level, Operation<Optional<RecipeHolder<T>>> original) {
        IBlockEntity be = (IBlockEntity) this;
        var stages = level.isClientSide ? null : be.stages();
        var recipes = RecipeThreadLocal.get();
        recipes.pushStages(stages);
        try {
            return original.call(instance, i, level);
        } finally {
            recipes.popStages();
        }
    }
}
