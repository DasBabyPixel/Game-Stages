package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.integration.kubejs.KJSScriptType;
import dev.latvian.mods.kubejs.script.ScriptType;

public class KJSUtil {
    public static KJSScriptType convert(ScriptType scriptType) {
        return switch (scriptType) {
            case STARTUP -> KJSScriptType.STARTUP;
            case SERVER -> KJSScriptType.SERVER;
            case CLIENT -> KJSScriptType.CLIENT;
        };
    }

    public static ScriptType convert(KJSScriptType scriptType) {
        return switch (scriptType) {
            case STARTUP -> ScriptType.STARTUP;
            case SERVER -> ScriptType.SERVER;
            case CLIENT -> ScriptType.CLIENT;
        };
    }
}
