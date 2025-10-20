package de.dasbabypixel.gamestages.common.integration.kubejs;

import java.util.function.Predicate;

public enum KJSScriptType implements Predicate<KJSScriptType> {
    STARTUP, SERVER, CLIENT;

    @Override
    public boolean test(KJSScriptType scriptType) {
        return scriptType == this;
    }
}
