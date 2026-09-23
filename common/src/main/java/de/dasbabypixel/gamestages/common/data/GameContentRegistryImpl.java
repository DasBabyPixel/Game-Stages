package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.attribute.AbstractCompilableAttributeHolder;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.data.attribute.AttributeCompiler;
import de.dasbabypixel.gamestages.common.data.attribute.AttributeEntry;
import de.dasbabypixel.gamestages.common.data.attribute.CompilableAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.CompilableAttributeHolder;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleImmutableAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleImmutableAttributeHolder;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class GameContentRegistryImpl extends SimpleImmutableAttributeHolder<GameContentRegistryImpl> implements GameContentRegistry {
    public GameContentRegistryImpl(Collection<AttributeEntry<? super GameContentRegistryImpl, ?>> attributes) {
        super(attributes);
    }

    @Override
    public List<Entry<?, ?, ?>> entries() {
        return get(Index.ATTRIBUTE).entries;
    }

    @Override
    public GameContentRegistry.Entry<?, ?, ?, ?> byTypeId(String id) {
        return Objects.requireNonNull(get(Index.ATTRIBUTE).entryByTypeId.get(id));
    }

    public static class Builder extends AbstractCompilableAttributeHolder<Builder, GameContentRegistryImpl> implements GameContentRegistry.Builder {
        private static final CompilableAttribute<Builder, List<Entry<?, ?, ?>>, GameContentRegistryImpl> ENTRY = (builder, value) -> builder.add(GameContentRegistryImpl.Index.ATTRIBUTE, new Index(value
                .stream()
                .map(CompilableAttributeHolder::compile)
                .toList()));

        {
            this.init(ENTRY, new ArrayList<>());
        }

        @Override
        public GameContentRegistry build() {
            return compile();
        }

        @Override
        public <TypeData, Elements, Element> GameContentRegistry.Builder.Entry<?, ?, TypeData, Elements, Element> register(String id, GameContentType<TypeData, Elements, Element> type) {
            var entry = new Entry<>(id, type);
            get(ENTRY).add(entry);
            return entry;
        }

        @Override
        public GameContentRegistryImpl compile(AttributeCompiler<Builder> compiler, CompiledAttributes<GameContentRegistryImpl> compiledAttributes) {
            return new GameContentRegistryImpl(compiledAttributes.attributes());
        }

        public static class Entry<TypeData, Elements, Element> extends AbstractCompilableAttributeHolder<Entry<TypeData, Elements, Element>, GameContentRegistryImpl.Entry<TypeData, Elements, Element>> implements GameContentRegistry.Builder.Entry<Entry<TypeData, Elements, Element>, GameContentRegistryImpl.Entry<TypeData, Elements, Element>, TypeData, Elements, Element> {
            private final String id;
            private final GameContentType<TypeData, Elements, Element> type;

            public Entry(String id, GameContentType<TypeData, Elements, Element> type) {
                this.id = id;
                this.type = type;
            }

            @Override
            public GameContentType<TypeData, Elements, Element> type() {
                return type;
            }

            @Override
            public String id() {
                return id;
            }

            @Override
            public GameContentRegistryImpl.Entry<TypeData, Elements, Element> compile(AttributeCompiler<Entry<TypeData, Elements, Element>> compiler, CompiledAttributes<GameContentRegistryImpl.Entry<TypeData, Elements, Element>> compiledAttributes) {
                return new GameContentRegistryImpl.Entry<>(compiledAttributes.attributes(), id, type);
            }
        }
    }

    public static class Entry<TypeData, Elements, Element> extends SimpleImmutableAttributeHolder<Entry<TypeData, Elements, Element>> implements GameContentRegistry.Entry<Entry<TypeData, Elements, Element>, TypeData, Elements, Element> {
        private final String id;
        private final GameContentType<TypeData, Elements, Element> type;
        private final GameContentDirect<TypeData, Elements, Element> empty;

        public Entry(Collection<AttributeEntry<? super Entry<TypeData, Elements, Element>, ?>> attributes, String id, GameContentType<TypeData, Elements, Element> type) {
            super(attributes);
            this.id = id;
            this.type = type;
            this.empty = new GameContentDirect<>(this, type.newElementsBuilder().build());
        }

        public GameContentType<TypeData, Elements, Element> type() {
            return type;
        }

        public String id() {
            return id;
        }

        @Override
        public GameContentDirect<TypeData, Elements, Element> empty() {
            return empty;
        }
    }

    private static class Index {
        private static final Attribute<GameContentRegistryImpl, Index> ATTRIBUTE = new SimpleImmutableAttribute<>();

        private final List<Entry<?, ?, ?>> entries;
        private final Map<String, Entry<?, ?, ?>> entryByTypeId;

        public Index(List<? extends Entry<?, ?, ?>> entries) {
            this.entries = List.copyOf(entries);
            var entryByTypeId = new HashMap<String, Entry<?, ?, ?>>();
            for (var entry : entries) {
                var old = entryByTypeId.put(entry.id(), entry);
                if (old != null) throw new IllegalStateException("Duplicate type ID: " + entry.id());
            }
            this.entryByTypeId = Map.copyOf(entryByTypeId);
        }
    }
}
