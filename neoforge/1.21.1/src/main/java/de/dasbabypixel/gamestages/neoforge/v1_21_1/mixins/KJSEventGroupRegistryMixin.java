package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import de.dasbabypixel.gamestages.common.integration.kubejs.event.KJSEventGroup;
import de.dasbabypixel.gamestages.common.integration.kubejs.event.KJSEventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("DataFlowIssue")
@Mixin(EventGroupRegistry.class)
@Implements(@Interface(iface = KJSEventGroupRegistry.class, prefix = "gamestages$"))
public interface KJSEventGroupRegistryMixin {
    @Shadow
    void register(EventGroup eventGroup);

    default void gamestages$register(KJSEventGroup group) {
        register((EventGroup) (Object) group);
    }
}
