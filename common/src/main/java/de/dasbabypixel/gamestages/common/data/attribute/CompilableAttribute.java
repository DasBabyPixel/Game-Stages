package de.dasbabypixel.gamestages.common.data.attribute;

import de.dasbabypixel.gamestages.common.Unit;
import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CompilableAttribute<H extends CompilableAttributeHolder<? extends H, ? extends CompiledHolder>, T, CompiledHolder extends AttributeHolder<? extends CompiledHolder>> extends Attribute<H, T>, CompilableResource<CompilableAttributeHolder.CompiledAttributesBuilder<? extends H, CompiledHolder>, Unit> {
    @Override
    default T get(H holder) {
        return holder.get(this);
    }

    @Override
    default Unit compile(CompilableAttributeHolder.CompiledAttributesBuilder<? extends H, CompiledHolder> builder) {
        compile(builder, builder.holder().get(this));
        return Unit.INSTANCE;
    }

    void compile(CompilableAttributeHolder.CompiledAttributesBuilder<? extends H, CompiledHolder> builder, T value);

    static <H extends CompilableAttributeHolder<? extends H, ? extends CH>, T, CH extends AttributeHolder<? extends CH>> CompilableAttribute<H, T, CH> noop() {
        // Workaround because attributes are based on object identity.
        class HC implements CompilableAttribute<H, T, CH> {
            @Override
            public void compile(CompilableAttributeHolder.CompiledAttributesBuilder<? extends H, CH> builder, T value) {
            }
        }
        return new HC();
    }
}
