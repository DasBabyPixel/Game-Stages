package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class AbstractCompilableAttributeHolder<H extends AbstractCompilableAttributeHolder<H, PC, C>, PC extends AbstractCompilableAttributeHolder.PreCompiled<?, C>, C> implements CompilableAttributeHolder<H, PC, C> {
    protected final Map<CompilableAttribute<? super H, ?, ?, ?, ?>, Object> attributeMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(CompilableAttribute<? super H, ?, ?, ?, T> attribute) {
        var val = (T) attributeMap.get(attribute);
        if (val != null) return val;
        val = Objects.requireNonNull(attribute.supply((H) this));
        attributeMap.put(attribute, val);
        return val;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(AttributeQuery<? super H, T> attribute) {
        return attribute.get((H) this);
    }

    @Override
    public PreCompiled<H> precompile(H h) {
        return new PreCompiled<>();
    }

    public static abstract class PreCompiled<H extends PreCompiled<H, C>, C extends Compiled<C>> extends ImmutableAttributeHolder<H> implements CompilableAttributeHolder.PreCompiled<H, C> {
        public PreCompiled(Map<ImmutableAttribute<? super H, ?>, Object> attributeMap) {
            super(attributeMap);
        }

        @Override
        public C compile(H h) {
            return new Compiled();
        }
    }

    public static class Compiled<H extends Compiled<H>> extends ImmutableAttributeHolder<H> {
        public Compiled(Map<ImmutableAttribute<? super H, ?>, Object> attributeMap) {
            super(attributeMap);
        }
    }
}
