package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class AbstractAttributeHolder<H extends AbstractAttributeHolder<H>> implements AttributeHolder<H> {
    protected final Map<Attribute<? super H, ?>, Object> attributeMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Attribute<? super H, T> attribute) {
        var val = (T) attributeMap.get(attribute);
        if (val != null) return val;
        val = Objects.requireNonNull(attribute.get((H) this));
        attributeMap.put(attribute, val);
        return val;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<AttributeEntry<? super H, ?>> attributes() {
        var list = new ArrayList<AttributeEntry<? super H, ?>>();
        for (var e : attributeMap.entrySet()) {
            Objects.requireNonNull(e);
            var key = e.getKey();
            list.add(new AttributeEntry<>((Attribute<H, @NonNull Object>) key, e.getValue()));
        }
        return List.copyOf(list);
    }
}
