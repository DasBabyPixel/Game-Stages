package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import com.llamalad7.mixinextras.sugar.Local;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@NullMarked
@Mixin(AbstractFurnaceBlockEntity.class)
public class MCAbstractFurnaceBlockEntityMixin {
    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Ljava/util/Optional;orElse(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static @Nullable Object stages$serverTick(Optional<?> instance, Object other, @Local(argsOnly = true) AbstractFurnaceBlockEntity blockEntity) {
        var recipeholder = (RecipeHolder<?>) instance.orElse(null);
        if (recipeholder != null) {
            var stages = blockEntity.stages();
            if (stages != null) {
                var entry = VRecipeAddon.getEntry(stages, recipeholder);
                if (entry != null) {
                    if (!entry.predicate().test()) {
                        return null;
                    }
                }
            }
        }
        return recipeholder;
    }
}
