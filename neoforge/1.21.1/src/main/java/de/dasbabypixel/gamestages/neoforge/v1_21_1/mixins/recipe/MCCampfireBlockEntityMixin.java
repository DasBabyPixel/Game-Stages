package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

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
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@NullMarked
@Mixin(CampfireBlockEntity.class)
public class MCCampfireBlockEntityMixin {
    @SuppressWarnings("UnnecessaryLocalVariable")
    @Redirect(method = "cookTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private static <T extends Recipe<I>, I extends RecipeInput> Optional<RecipeHolder<T>> cookTick(RecipeManager.CachedCheck<I, T> instance, I i, Level level, @Local(argsOnly = true) CampfireBlockEntity blockEntity) {
        IBlockEntity be = blockEntity;
        var recipes = RecipeThreadLocal.get();
        recipes.stages(be.stages());
        try {
            return instance.getRecipeFor(i, level);
        } finally {
            recipes.clearStages();
        }
    }
}
