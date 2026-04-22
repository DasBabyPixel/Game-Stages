package de.dasbabypixel.gamestages.common.data.manager.immutable;

import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Set;

@NullMarked
public final class TypeIndex<Type extends TypedGameContent> {
    public static final TypeIndex<?> EMPTY = new TypeIndex<>(Map.of(), Set.of());
    private final Map<Object, RestrictionEntry.PreCompiled<?, ?>> preCompiledByContent;
    private final Set<RestrictionEntry.PreCompiled<?, ?>> entries;

    public TypeIndex(Map<Object, RestrictionEntry.PreCompiled<?, ?>> preCompiledByContent, Set<RestrictionEntry.PreCompiled<?, ?>> entries) {
        this.preCompiledByContent = Map.copyOf(preCompiledByContent);
        this.entries = Set.copyOf(entries);
    }

    public Map<Object, RestrictionEntry.PreCompiled<?, ?>> preCompiledByContent() {
        return preCompiledByContent;
    }

    @SuppressWarnings("unchecked")
    public <PC extends RestrictionEntry.PreCompiled<?, ?>> Set<PC> entries() {
        return (Set<PC>) entries;
    }
}
