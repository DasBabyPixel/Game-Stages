package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
public final class AttributeCompiler extends AbstractAttributeHolder<AttributeCompiler> {
    private final Typed<?> typed;

    public <Holder> AttributeCompiler(Map<CompilableAttribute<? super Holder, ?, ?, ?>, Object> attributeMap) {
        typed = new Typed<>(attributeMap);
    }

    private static class Typed<Holder> {
        private final Map<CompilableAttribute<? super Holder, ?, ?, ?>, Object> attributeMap;

        public Typed(Map<CompilableAttribute<? super Holder, ?, ?, ?>, Object> attributeMap) {
            this.attributeMap = Map.copyOf(attributeMap);
        }

        void compile() {

        }
    }
}
