package de.dasbabypixel.gamestages.common.integration.kubejs.event;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.integration.kubejs.KJSScriptType;
import dev.latvian.mods.kubejs.event.KubeEvent;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface KJSEventGroup {
    static KJSEventGroup of(String name) {
        return CommonInstances.kjsCommonProvider.eventGroupOf(name);
    }

    Map<String, ? extends KJSEventHandler> handlerMap();

    KJSEventHandler add(String name, Predicate<KJSScriptType> scriptTypeFilter, Supplier<Class<? extends KubeEvent>> eventTypeSupplier);

    default KJSEventHandler server(String name, Supplier<Class<? extends KubeEvent>> eventTypeSupplier) {
        return add(name, KJSScriptType.SERVER, eventTypeSupplier);
    }

    default KJSEventHandler client(String name, Supplier<Class<? extends KubeEvent>> eventTypeSupplier) {
        return add(name, KJSScriptType.CLIENT, eventTypeSupplier);
    }

    default KJSEventHandler startup(String name, Supplier<Class<? extends KubeEvent>> eventTypeSupplier) {
        return add(name, KJSScriptType.STARTUP, eventTypeSupplier);
    }

    default KJSEventHandler common(String name, Supplier<Class<? extends KubeEvent>> eventTypeSupplier) {
        return add(name, ignored -> true, eventTypeSupplier);
    }
}
