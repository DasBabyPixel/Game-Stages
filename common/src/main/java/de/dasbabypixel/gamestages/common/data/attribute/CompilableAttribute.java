package de.dasbabypixel.gamestages.common.data.attribute;

import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@NullMarked
public record CompilableAttribute<H extends CompilableAttributeHolder<H, ?>, C, CAttribute extends AttributeQuery<?, C>, T>(
        Function<H, T> defaultValue, Function<AttributeCompiler, C> compiler,
        CAttribute compiledAttribute) implements CompilableResource<AttributeCompiler, CompilableAttribute.Compiled<C, CAttribute>>, AttributeQuery<H, T> {

    @Override
    public T get(H holder) {
        return holder.get(this);
    }

    public T supply(H holder) {
        return defaultValue.apply(holder);
    }

    @Override
    public Compiled<C, CAttribute> compile(AttributeCompiler attributeCompiler) {
        return new Compiled<>(compiler.apply(attributeCompiler), compiledAttribute);
    }

    public record Compiled<C, CA>(C compiled, CA attribute) {
    }
}
