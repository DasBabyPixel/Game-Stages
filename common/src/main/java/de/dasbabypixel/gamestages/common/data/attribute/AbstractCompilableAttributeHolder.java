package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class AbstractCompilableAttributeHolder<Self extends AbstractCompilableAttributeHolder<Self, C>, C> implements CompilableAttributeHolder<Self, C> {
    private final Map<CompilableAttribute<? super Self, ?, ?, ?>, Object> attributeMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(CompilableAttribute<? super Self, ?, ?, T> attribute) {
        var val = (T) attributeMap.get(attribute);
        if (val != null) return val;
        val = attribute.supply(self());
        attributeMap.put(attribute, val);
        return val;
    }

    @Override
    public C compile(Self self) {
        var compiledList = new ArrayList<CompilableAttribute.Compiled<?, ?>>();
        for (var entry : attributeMap.entrySet()) {
            Objects.requireNonNull(entry);
            var attribute = entry.getKey();
        }
        return null;
    }
}
