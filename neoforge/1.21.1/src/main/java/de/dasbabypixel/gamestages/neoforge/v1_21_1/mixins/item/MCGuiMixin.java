package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.item;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.VItemAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.settings.VCompiledItemStackRestrictionEntrySettings;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.settings.VHiddenName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.UnaryOperator;

@NullMarked
@Mixin(Gui.class)
public class MCGuiMixin {
    @Shadow
    private ItemStack lastToolHighlight;
    @Final
    @Shadow
    private Minecraft minecraft;

    @Unique
    private boolean stages$overriddenDisplayName = false;

    @Redirect(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;withStyle(Ljava/util/function/UnaryOperator;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent withStyle(MutableComponent instance, UnaryOperator<Style> modifyFunc) {
        stages$overriddenDisplayName = false;
        var player = minecraft.player;
        if (player != null) {
            var stages = player.getGameStages();
            var entry = VItemAddon.getEntry(stages, lastToolHighlight, lastToolHighlight);
            if (entry != null && !entry.predicate().test()) {
                var hiddenName = ((VCompiledItemStackRestrictionEntrySettings) entry.settings()).hiddenName();
                if (hiddenName.hiddenName()) {
                    var newName = hiddenName.function()
                            .getHiddenName(new VHiddenName.FunctionData(player, entry, lastToolHighlight));
                    if (newName == null) {
                        return instance.withStyle(modifyFunc);
                    }
                    stages$overriddenDisplayName = true;
                    return newName.copy();
                }
            }
        }
        return instance.withStyle(modifyFunc);
    }

    @Redirect(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z"))
    private boolean has(ItemStack instance, DataComponentType<Component> dataComponentType) {
        if (stages$overriddenDisplayName) return false;
        return instance.has(dataComponentType);
    }

    @Redirect(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/extensions/common/IClientItemExtensions;getFont(Lnet/minecraft/world/item/ItemStack;Lnet/neoforged/neoforge/client/extensions/common/IClientItemExtensions$FontContext;)Lnet/minecraft/client/gui/Font;"))
    private @Nullable Font getFont(IClientItemExtensions instance, ItemStack stack, IClientItemExtensions.FontContext context) {
        if (stages$overriddenDisplayName) return null;
        return instance.getFont(stack, context);
    }
}
