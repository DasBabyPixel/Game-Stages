package de.dasbabypixel.gamestages.common.data.manager.mutable.compiler;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.data.attribute.AttributeQuery;
import de.dasbabypixel.gamestages.common.data.manager.immutable.PreCompileIndex;
import de.dasbabypixel.gamestages.common.data.manager.immutable.TypeIndex;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@NullMarked
public final class MutablePreCompileIndex {
    private static final Attribute<ManagerCompilerTask, MutablePreCompileIndex> INTERNAL_ATTRIBUTE = new Attribute<>(MutablePreCompileIndex::new);
    public static final AttributeQuery<ManagerCompilerTask, MutablePreCompileIndex> ATTRIBUTE = h -> h.get(INTERNAL_ATTRIBUTE);

    private final Set<RestrictionEntry.PreCompiled<?, ?>> entries = new HashSet<>();
    private final Map<GameContentType<?>, MutableTypeIndex<?>> typeIndexMap = new HashMap<>();

    public PreCompileIndex compile() {
        var typeIndexMap = new HashMap<GameContentType<?>, TypeIndex<?>>();
        for (var entry : this.typeIndexMap.entrySet()) {
            Objects.requireNonNull(entry);
            typeIndexMap.put(entry.getKey(), entry.getValue().compile());
        }
        return new PreCompileIndex(entries, typeIndexMap);
    }

    public Set<RestrictionEntry.PreCompiled<?, ?>> preCompiledRestrictions() {
        return entries;
    }

    @SuppressWarnings("unchecked")
    public <Type extends TypedGameContent> MutableTypeIndex<Type> typeIndex(GameContentType<Type> type) {
        return (MutableTypeIndex<Type>) typeIndexMap.computeIfAbsent(type, ignored -> new MutableTypeIndex<>());
    }

    public Map<GameContentType<?>, MutableTypeIndex<?>> typeIndexMap() {
        return typeIndexMap;
    }
}
