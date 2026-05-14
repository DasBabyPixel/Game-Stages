package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.integration.exdeorum;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.exdeorum.IXeiSieveRecipeResult;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thedarkcolour.exdeorum.compat.XeiSieveRecipe;

import java.util.Objects;

@NullMarked
@Mixin(targets = "thedarkcolour/exdeorum/compat/jei/SieveCategory")
public class EDSieveCategoryMixin {
    @SuppressWarnings({"DataFlowIssue"})
    @Inject(method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lthedarkcolour/exdeorum/compat/XeiSieveRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"), cancellable = true)
    private void set(IRecipeLayoutBuilder builder, XeiSieveRecipe recipe, IFocusGroup focuses, CallbackInfo ci) {

        var player = Minecraft.getInstance().player;
        var stages = player.getGameStages();
        for (int i = 0; i < recipe.results().size(); i++) {
            XeiSieveRecipe.Result result = Objects.requireNonNull(recipe.results().get(i));
            var ir = (IXeiSieveRecipeResult) (Object) result;
            var holder = ir.holder();
            var entry = VRecipeAddon.getEntry(stages, holder);
            if (entry != null) {
                if (!entry.predicate().test()) {
                    continue;
                }
            }

            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.OUTPUT, 1 + i % 9 * 18, 29 + 18 * (i / 9))
                    .addItemStack(Objects.requireNonNull(result.item));
            addTooltips(slot, result.byHandOnly, result.provider);
        }

        ci.cancel();
    }

    @Shadow
    public static void addTooltips(@Nullable IRecipeSlotBuilder slot, boolean byHandOnly, @Nullable NumberProvider numberProvider) {
        throw new UnsupportedOperationException();
    }
}
