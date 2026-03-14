package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.HashMap;
import java.util.Map;

public final class EventType<Event extends EventJSBase<Event>> {
    private final Class<? extends Event> cls;
    private final TypeInfo type;
    private Map<String, BaseFunction> functions = new HashMap<>();

    public EventType(Class<? extends Event> cls) {
        this.cls = cls;
        this.type = TypeInfo.of(cls);
    }

    public void addFunction(String name, EventJSBase.Function<? super Event> function) {
        functions.put(name, new BaseFunction() {
            @Override
            public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                return function.invoke(cls.cast(cx.jsToJava(thisObj, type)), (KubeJSContext) cx, args);
            }
        });
    }

    public void freeze() {
        functions = Map.copyOf(functions);
    }

    public Map<String, BaseFunction> functions() {
        return functions;
    }
}
