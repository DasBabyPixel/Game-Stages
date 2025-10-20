package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import de.dasbabypixel.gamestages.common.integration.kubejs.KJSScriptType;
import de.dasbabypixel.gamestages.common.integration.kubejs.event.KJSEventGroup;
import de.dasbabypixel.gamestages.common.integration.kubejs.event.KJSEventHandler;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.KJSUtil;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.ScriptTypePredicate;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "RedundantCast"})
@Mixin(EventGroup.class)
@Implements(@Interface(iface = KJSEventGroup.class, prefix = "gamestages$"))
public abstract class KJSEventGroupMixin {
    @Shadow
    public abstract Map<String, EventHandler> getHandlers();

    @Shadow
    public abstract EventHandler add(String name, ScriptTypePredicate scriptType, Supplier<Class<? extends KubeEvent>> eventType);

    public Map<String, ? extends KJSEventHandler> gamestages$handlerMap() {
        return (Map<String, ? extends KJSEventHandler>) (Object) getHandlers();
    }

    public KJSEventHandler gamestages$add(String name, Predicate<KJSScriptType> scriptTypeFilter, Supplier<Class<? extends KubeEvent>> eventTypeSupplier) {
        return (KJSEventHandler) add(name, scriptType -> scriptTypeFilter.test(KJSUtil.convert(scriptType)), eventTypeSupplier);
    }
}
