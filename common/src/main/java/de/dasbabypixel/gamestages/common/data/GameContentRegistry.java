package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.attribute.ImmutableAttributeHolder;
import de.dasbabypixel.gamestages.common.data.attribute.MutableCompilableAttributeHolder;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface GameContentRegistry {
    static Builder builder() {
        return new GameContentRegistryImpl.Builder();
    }

    List<? extends Entry<?, ?, ?, ?>> entries();

    Entry<?, ?, ?, ?> byTypeId(String id);

    interface Builder {
        <TypeData, Elements, Element> Entry<?, ?, TypeData, Elements, Element> register(String id, GameContentType<TypeData, Elements, Element> type);

        GameContentRegistry build();

        interface Entry<Self extends Entry<Self, Compiled, TypeData, Elements, Element>, Compiled extends GameContentRegistry.Entry<Compiled, TypeData, Elements, Element>, TypeData, Elements, Element> extends MutableCompilableAttributeHolder<Self, Compiled> {
            GameContentType<TypeData, Elements, Element> type();

            String id();
        }
    }

    interface Entry<Self extends Entry<Self, TypeData, Elements, Element>, TypeData, Elements, Element> extends ImmutableAttributeHolder<Self> {
        GameContentType<TypeData, Elements, Element> type();

        String id();

        GameContentDirect<TypeData, Elements, Element> empty();
    }
}
