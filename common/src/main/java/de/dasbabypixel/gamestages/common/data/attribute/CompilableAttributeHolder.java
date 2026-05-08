package de.dasbabypixel.gamestages.common.data.attribute;

import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@NullMarked
public interface CompilableAttributeHolder<Self extends CompilableAttributeHolder<? extends Self, ? extends CompiledHolder>, CompiledHolder extends AttributeHolder<? extends CompiledHolder>> extends AttributeHolder<Self>, CompilableResource<AttributeCompiler<Self>, CompiledHolder> {
    default CompiledHolder compile() {
        return compile(new AttributeCompiler<>(self()));
    }

    @SuppressWarnings("unchecked")
    @Override
    default CompiledHolder compile(AttributeCompiler<Self> compiler) {
        var builder = new CompiledAttributesBuilder<>(compiler);

        for (var attributeEntry : this.attributes()) {
            var attribute = ((CompilableAttribute<Self, ?, CompiledHolder>) attributeEntry.attribute());
            attribute.compile(builder);
        }
        postCompile(builder);
        var compiledAttributes = new CompiledAttributes<>(builder.attributes);
        return compile(compiler, compiledAttributes);
    }

    default void postCompile(CompiledAttributesBuilder<Self, CompiledHolder> builder) {
    }

    @Override
    Collection<AttributeEntry<? super Self, ?>> attributes();

    <T> T get(CompilableAttribute<? super Self, T, ?> attribute);

    CompiledHolder compile(AttributeCompiler<Self> compiler, CompiledAttributes<CompiledHolder> compiledAttributes);

    final class CompiledAttributesBuilder<OrigHolder extends CompilableAttributeHolder<? extends OrigHolder, ? extends CompiledHolder>, CompiledHolder extends AttributeHolder<? extends CompiledHolder>> {
        private final AttributeCompiler<OrigHolder> compiler;
        private final List<AttributeEntry<? super CompiledHolder, ?>> attributes = new ArrayList<>();

        public CompiledAttributesBuilder(AttributeCompiler<OrigHolder> compiler) {
            this.compiler = compiler;
        }

        public AttributeCompiler<OrigHolder> compiler() {
            return compiler;
        }

        public OrigHolder holder() {
            return compiler.holder();
        }

        public void add(AttributeEntry<? super CompiledHolder, ?> entry) {
            attributes.add(entry);
        }

        public <T> void add(Attribute<? super CompiledHolder, T> attribute, T value) {
            add(new AttributeEntry<>(attribute, value));
        }

        @SuppressWarnings("unchecked")
        public <NO extends CompilableAttributeHolder<? extends NO, ? extends NC>, NC extends AttributeHolder<? extends NC>> CompiledAttributesBuilder<NO, NC> cast(NO holder) {
            return (CompiledAttributesBuilder<NO, NC>) this;
        }
    }

    record CompiledAttributes<CompiledHolder>(List<AttributeEntry<? super CompiledHolder, ?>> attributes) {
        public CompiledAttributes {
            attributes = Objects.requireNonNull(List.copyOf(attributes));
        }
    }
}
