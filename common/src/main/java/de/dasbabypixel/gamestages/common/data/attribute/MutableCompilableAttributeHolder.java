package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MutableCompilableAttributeHolder<Self extends MutableCompilableAttributeHolder<? extends Self, ? extends CompiledHolder>, CompiledHolder extends AttributeHolder<? extends CompiledHolder>> extends CompilableAttributeHolder<Self, CompiledHolder> {
    <T> void init(CompilableAttribute<? super Self, T, ?> attribute, T value);
}
