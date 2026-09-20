package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe.integration.polymorph;

import com.illusivesoulworks.polymorph.common.capability.AbstractRecipeData;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeThreadLocal;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

@NullMarked
@Mixin(value = AbstractRecipeData.class, remap = false)
public abstract class PolymorphAbstractRecipeDataMixin {
    @ModifyExpressionValue(method = "getRecipe", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private <T extends Recipe<I>, I extends RecipeInput> Iterator<RecipeHolder<T>> getRecipe(Iterator<RecipeHolder<T>> original) {
        var stages = RecipeThreadLocal.get().stagesOrRecord();
        return new Iterator<>() {
            private @Nullable RecipeHolder<T> next;

            private void load() {
                if (next != null) return;
                while (true) {
                    if (!original.hasNext()) {
                        return;
                    }
                    var n = original.next();
                    var entry = VRecipeAddon.getEntry(stages, n);
                    if (entry == null || entry.predicate().test()) {
                        next = n;
                        return;
                    }
                }
            }

            @Override
            public boolean hasNext() {
                load();
                return next != null;
            }

            @Override
            public RecipeHolder<T> next() {
                if (!hasNext()) throw new NoSuchElementException();
                var n = Objects.requireNonNull(next);
                next = null;
                return n;
            }
        };
    }
}
