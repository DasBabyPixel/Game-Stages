package de.dasbabypixel.gamestages.common.integration.kubejs.binding;

import de.dasbabypixel.gamestages.common.integration.kubejs.KJSScriptType;

public interface KJSBindingRegistry {
    KJSScriptType scriptType();

    void register(String name, Object value);
}
