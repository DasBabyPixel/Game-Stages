package de.dasbabypixel.gamestages.common.data.manager.mutable.compiler;

import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.manager.immutable.TypeIndex;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NullMarked
public class MutableTypeIndex<Type extends TypedGameContent> {
    private final Map<Object, RestrictionEntry.PreCompiled<?, ?>> preCompiledByContent = new HashMap<>();
    private final Set<RestrictionEntry.PreCompiled<?, ?>> entries = new HashSet<>();

    public Map<Object, RestrictionEntry.PreCompiled<?, ?>> preCompiledByContent() {
        return preCompiledByContent;
    }

    @SuppressWarnings("unchecked")
    public <PC extends RestrictionEntry.PreCompiled<?, ?>> Set<PC> entries() {
        return (Set<PC>) entries;
    }

    public TypeIndex<Type> compile() {
        return new TypeIndex<>(preCompiledByContent, entries);
    }
}
