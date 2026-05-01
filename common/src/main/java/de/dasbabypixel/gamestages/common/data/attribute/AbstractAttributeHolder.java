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
public class AbstractAttributeHolder<H extends AbstractAttributeHolder<H>> implements AttributeHolder<H> {
    protected final Map<Attribute<? super H, ?>, Object> attributeMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Attribute<? super H, T> attribute) {
        var val = (T) attributeMap.get(attribute);
        if (val != null) return val;
        val = Objects.requireNonNull(attribute.supply((H) this));
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
        return Objects.requireNonNull(List.copyOf(list));
    }
}
