package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import de.dasbabypixel.gamestages.common.integration.kubejs.KJSScriptType;
import de.dasbabypixel.gamestages.common.integration.kubejs.binding.KJSBindingRegistry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.KJSUtil;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptType;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BindingRegistry.class)
@Implements(@Interface(iface = KJSBindingRegistry.class, prefix = "gamestages$"))
public abstract class KJSBindingRegistryMixin {
    @Shadow
    public abstract ScriptType type();

    @Shadow
    public abstract void add(String name, Object value);

    public KJSScriptType gamestages$scriptType() {
        return KJSUtil.convert(type());
    }

    public void gamestages$register(String name, Object value) {
        add(name, value);
    }
}
