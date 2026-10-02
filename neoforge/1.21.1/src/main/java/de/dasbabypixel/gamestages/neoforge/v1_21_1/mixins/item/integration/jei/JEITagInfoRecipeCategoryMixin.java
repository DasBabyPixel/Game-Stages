package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.item.integration.jei;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.VItemAddon;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.library.plugins.jei.tags.TagInfoRecipeCategory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

@NullMarked
@Mixin(TagInfoRecipeCategory.class)
public class JEITagInfoRecipeCategoryMixin {
    @WrapOperation(method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lmezz/jei/library/plugins/jei/tags/ITagInfoRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private Iterator<ITypedIngredient<?>> setRecipe(List<ITypedIngredient<?>> instance, Operation<Iterator<ITypedIngredient<?>>> original) {
        var it = original.call(instance);

        return new Iterator<>() {
            private @Nullable ITypedIngredient<?> next;

            private void load() {
                if (next != null) return;
                while (it.hasNext()) {
                    next = it.next();

                    if (next.getType() == VanillaTypes.ITEM_STACK) {
                        var itemStackTyped = next.castToItemStackType();
                        if (itemStackTyped != null) {
                            var itemStack = itemStackTyped.getIngredient();
                            var entry = VItemAddon.getEntry(ClientGameStageManager.stages(), itemStack, itemStack);
                            if (entry != null && !entry.predicate().test()) {
                                next = null;
                                continue;
                            }
                        }
                    }
                    break;
                }
            }

            @Override
            public boolean hasNext() {
                load();
                return next != null;
            }

            @Override
            public ITypedIngredient<?> next() {
                load();
                var n = next;
                next = null;
                if (n == null) throw new NoSuchElementException();
                return n;
            }
        };
    }
}
