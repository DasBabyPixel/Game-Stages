package de.dasbabypixel.gamestages.common.data.attribute;

import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@NullMarked
public record CompilableAttribute<SharedH extends CompilableAttributeHolder<SharedH, ?, ?>, SingleH, PC extends CompilableResource.PreCompiled<SingleH, C>, C, T>(
        Function<SharedH, T> defaultValue,
        Function<AttributeCompiler, PC> precompiler) implements CompilableResource<AttributeCompiler, PC, SingleH, C>, AttributeQuery<SharedH, T> {
    @Override
    public PC precompile(AttributeCompiler sharedH) {
        return precompiler.apply(sharedH);
    }

    @Override
    public T get(SharedH holder) {
        return holder.get(this);
    }

    public T supply(SharedH holder) {
        return defaultValue.apply(holder);
    }
}
