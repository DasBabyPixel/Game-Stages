package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public abstract class AbstractCompilableAttributeHolder<Self extends AbstractCompilableAttributeHolder<? extends Self, ? extends CompiledHolder>, CompiledHolder extends AttributeHolder<? extends CompiledHolder>> implements CompilableAttributeHolder<Self, CompiledHolder> {
    protected final Map<CompilableAttribute<? super Self, ?, ?>, Object> attributeMap = new HashMap<>();

    public <T> void init(CompilableAttribute<? super Self, T, ?> attribute, T value) {
        if (attributeMap.containsKey(attribute)) throw new IllegalStateException();
        attributeMap.put(attribute, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(CompilableAttribute<? super Self, T, ?> attribute) {
        return Objects.requireNonNull((T) attributeMap.get(attribute));
    }

    @Override
    public boolean has(CompilableAttribute<? super Self, ?, ?> attribute) {
        return attributeMap.containsKey(attribute);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<AttributeEntry<? super Self, ?>> attributes() {
        var list = new ArrayList<AttributeEntry<? super Self, ?>>();
        for (var e : attributeMap.entrySet()) {
            Objects.requireNonNull(e);
            Attribute<? super Self, Object> key = (Attribute<? super Self, @NonNull Object>) e.getKey();
            list.add(new AttributeEntry<>(key, e.getValue()));
        }
        return Objects.requireNonNull(List.copyOf(list));
    }
}
