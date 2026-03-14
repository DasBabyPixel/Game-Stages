package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.RegisterEventJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.ScriptableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ScriptManager.class)
public class KJSScriptManagerMixin {
    @Inject(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;<init>()V", shift = At.Shift.BEFORE))
    public void load(long startAll, CallbackInfo ci, @Local(name = "cx") KubeJSContext cx) {
        var scope = cx.topLevelScope;

        var clazz = cx.loadJavaClass(RegisterEventJS.class.getName(), true);
        System.out.println(clazz.get(cx, "prototype", clazz));
        System.out.println(ScriptableObject.getProperty(clazz, "prototype", cx));
        System.out.println(clazz);
        System.out.println(clazz);
        System.out.println(Arrays.toString(clazz.getAllIds(cx)));
        var proto = clazz.getPrototype(cx);
        System.out.println(proto);
        System.out.println(proto);
        System.out.println(proto);
        System.out.println(proto);
    }
}
