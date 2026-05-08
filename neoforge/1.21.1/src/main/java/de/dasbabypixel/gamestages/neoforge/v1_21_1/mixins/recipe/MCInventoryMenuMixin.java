package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import de.dasbabypixel.gamestages.common.v1_21_1.data.StageRefreshableMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultContainer;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@NullMarked
@Implements(@Interface(iface = StageRefreshableMenu.class, prefix = "stages$"))
@Mixin(InventoryMenu.class)
public final class MCInventoryMenuMixin {
    @Final
    @Shadow
    private Player owner;
    @Final
    @Shadow
    private CraftingContainer craftSlots;
    @Final
    @Shadow
    private ResultContainer resultSlots;

    public void stages$refresh() {
        CraftingMenu.slotChangedCraftingGrid((InventoryMenu) (Object) this, owner.level(), this.owner, this.craftSlots, this.resultSlots, null);
    }
}
