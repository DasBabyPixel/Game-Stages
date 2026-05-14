package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.listener;

import com.mojang.datafixers.util.Either;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.VItemAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.settings.VCompiledItemStackRestrictionEntrySettings;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.settings.VHiddenName;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class ClientEventListener {
    public static void register() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ClientEventListener::gather);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ClientEventListener::onItemTooltip);
    }

    private static void gather(RenderTooltipEvent.GatherComponents event) {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if (player == null) return;
        var stages = player.getGameStages();
        var itemStack = event.getItemStack();
        var entry = VItemAddon.getEntry(stages, itemStack, itemStack);
        if (entry == null) return;
        if (entry.predicate().test()) return; // Unlocked
        var settings = ((VCompiledItemStackRestrictionEntrySettings) entry.settings());
        var hiddenName = settings.hiddenName();
        if (!hiddenName.hiddenName()) return;
        var newName = hiddenName.function().getHiddenName(new VHiddenName.FunctionData(player, entry, itemStack));
        if (newName == null) return;
        var anyRight = event.getTooltipElements()
                .stream()
                .anyMatch(e -> Objects.requireNonNull(Objects.requireNonNull(e).right()).isPresent());
        if (!anyRight) {
            event.getTooltipElements().clear();
            event.getTooltipElements().add(Either.left(newName));
        }
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
//        var player = event.getEntity();
//        if (player == null) return;
//        if (Mods.JEI.isLoaded()) {
//            if (JEIIntegration.isReloading) {
//                return; // We don't want to interfere with JEI
//            }
//        }
//        var context = event.getContext();
//        if (context.level() == null) return;
//        var stages = player.getGameStages();
//        var itemStack = event.getItemStack();
//        var entry = VItemAddon.getEntry(stages, itemStack, itemStack);
//        if (entry == null) return;
//        var settings = ((VCompiledItemStackRestrictionEntrySettings) entry.settings());
//        var hiddenName = settings.hiddenName();
//        if (!hiddenName.hiddenName()) return;
//        var newName = hiddenName.function().getHiddenName(new VHiddenName.FunctionData(player, entry, itemStack));
//        if (newName == null) return;
//        event.getToolTip().clear();
//        event.getToolTip().add(newName);
    }
}
