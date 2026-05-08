package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

import java.util.function.BiFunction;

@NullMarked
public interface ImmutableAttribute<H extends ImmutableAttributeHolder<? extends H>, T> extends Attribute<H, T> {
    default <A extends CompilableAttributeHolder<? extends A, ? extends H>, CT> CompilableAttribute<A, CT, H> compilable(BiFunction<CompilableAttributeHolder.CompiledAttributesBuilder<? extends A, H>, CT, T> compiler) {
        return (builder, value) -> builder.add(this, compiler.apply(builder, value));
    }

    default <A extends CompilableAttributeHolder<? extends A, ? extends H>> CompilableAttribute<A, T, H> compilable() {
        return compilable((a, b) -> b);
    }

    @Override
    default T get(H holder) {
        return holder.get(this);
    }
}
