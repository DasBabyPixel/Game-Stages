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
public class SimpleAttributeHolder<H extends SimpleAttributeHolder<? extends H>> implements AttributeHolder<H> {
    protected final Map<SimpleAttribute<? super H, ?>, Object> attributeMap = new HashMap<>();

    public <T> T init(SimpleAttribute<? super H, T> attribute, T value) {
        if (attributeMap.containsKey(attribute)) throw new IllegalStateException();
        attributeMap.put(attribute, value);
        return value;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(SimpleAttribute<? super H, T> attribute) {
        return Objects.requireNonNull((T) attributeMap.get(attribute));
    }

    public boolean has(SimpleAttribute<? super H, ?> attribute) {
        return attributeMap.containsKey(attribute);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<AttributeEntry<? super H, ?>> attributes() {
        var list = new ArrayList<AttributeEntry<? super H, ?>>();
        for (var e : attributeMap.entrySet()) {
            Objects.requireNonNull(e);
            Attribute<? super H, Object> key = (Attribute<? super H, @NonNull Object>) e.getKey();
            list.add(new AttributeEntry<>(key, e.getValue()));
        }
        return Objects.requireNonNull(List.copyOf(list));
    }
}
