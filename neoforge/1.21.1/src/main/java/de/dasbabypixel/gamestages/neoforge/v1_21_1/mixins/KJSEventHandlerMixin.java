package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import de.dasbabypixel.gamestages.common.integration.kubejs.event.KJSEventHandler;
import dev.latvian.mods.kubejs.event.EventHandler;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EventHandler.class)
@Implements(@Interface(iface = KJSEventHandler.class, prefix = "gamestages$"))
public class KJSEventHandlerMixin {

}
