package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class ReplaceableImmutableAttributeHolder<Self extends ReplaceableImmutableAttributeHolder<Self>> implements ImmutableAttributeHolder<Self> {
    protected final Map<ImmutableAttribute<? super Self, ?>, Object> attributeMap = new HashMap<>();
    private final List<AttributeEntry<? super Self, ?>> attributeEntries = new ArrayList<>();

    public ReplaceableImmutableAttributeHolder() {
    }

    public <T> T init(ImmutableAttribute<? super Self, T> attribute, T value) {
        if (attributeMap.containsKey(attribute)) throw new IllegalStateException();
        attributeMap.put(attribute, value);
        attributeEntries.add(new AttributeEntry<>(attribute, value));
        return value;
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

    public void reset() {
        attributeMap.clear();
        attributeEntries.clear();
    }
}
