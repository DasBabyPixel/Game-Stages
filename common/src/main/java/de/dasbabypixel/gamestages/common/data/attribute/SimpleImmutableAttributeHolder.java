package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class SimpleImmutableAttributeHolder<Self extends SimpleImmutableAttributeHolder<? extends Self>> implements ImmutableAttributeHolder<Self> {
    protected final Map<ImmutableAttribute<? super Self, ?>, Object> attributeMap;
    private final List<AttributeEntry<? super Self, ?>> attributeEntries;

    public SimpleImmutableAttributeHolder(Collection<AttributeEntry<? super Self, ?>> attributes) {
        this.attributeEntries = Objects.requireNonNull(List.copyOf(attributes));
        var map = new HashMap<ImmutableAttribute<? super Self, ?>, Object>();
        for (var entry : attributeEntries) {
            map.put((ImmutableAttribute<? super Self, ?>) entry.attribute(), entry.value());
        }
        this.attributeMap = Objects.requireNonNull(Map.copyOf(map));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(ImmutableAttribute<? super Self, T> attribute) {
        return Objects.requireNonNull((T) attributeMap.get(attribute));
    }

    @Override
    public Collection<AttributeEntry<? super Self, ?>> attributes() {
        return attributeEntries;
    }
}
