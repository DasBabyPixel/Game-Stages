package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
public class ImmutableAttributeHolder<H extends ImmutableAttributeHolder<H>> implements IAttributeHolder<H> {
    private final Map<ImmutableAttribute<? super H, ?>, Object> attributeMap;

    public ImmutableAttributeHolder(Map<ImmutableAttribute<? super H, ?>, Object> attributeMap) {
        this.attributeMap = Map.copyOf(attributeMap);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ImmutableAttribute<H, T> attribute) {
        var val = (T) attributeMap.get(attribute);
        if (val == null) throw new IllegalStateException("Attribute not stored on this immutable holder");
        return val;
    }
}
