package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
@Implements(@Interface(iface = de.dasbabypixel.gamestages.common.data.ItemStack.class, prefix = "stages$"))
@NullMarked
public class MCItemStackMixin implements de.dasbabypixel.gamestages.common.data.ItemStack {
}
