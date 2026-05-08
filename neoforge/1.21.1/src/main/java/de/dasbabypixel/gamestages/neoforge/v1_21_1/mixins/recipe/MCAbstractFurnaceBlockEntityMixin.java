package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.sugar.Local;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeThreadLocal;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@NullMarked
@Mixin(AbstractFurnaceBlockEntity.class)
public class MCAbstractFurnaceBlockEntityMixin {
    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private static <T extends Recipe<I>, I extends RecipeInput> Optional<RecipeHolder<T>> stages$serverTick(RecipeManager.CachedCheck<I, T> instance, I i, Level level, @Local(argsOnly = true) AbstractFurnaceBlockEntity blockEntity) {
        var stages = blockEntity.stages();
        if (stages != null) {
            // TODO replace with scoped values
            var l = RecipeThreadLocal.get();
            l.stages(stages);
            try {
                return instance.getRecipeFor(i, level);
            } finally {
                l.clearStages();
            }
        }
        // TODO multiple similar recipes could be registered, this just returns one at random...
        return instance.getRecipeFor(i, level);
    }
}
