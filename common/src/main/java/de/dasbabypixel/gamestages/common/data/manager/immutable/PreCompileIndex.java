package de.dasbabypixel.gamestages.common.data.manager.immutable;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.attribute.ImmutableAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleImmutableAttribute;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@NullMarked
public final class PreCompileIndex {
    public static final ImmutableAttribute<AbstractGameStageManager<?>, PreCompileIndex> ATTRIBUTE = new SimpleImmutableAttribute<>();
    private final Set<RestrictionEntry.PreCompiled<?, ?>> entries;
    private final Map<GameContentType<?>, TypeIndex<?>> typeIndexMap;

    public PreCompileIndex(Set<RestrictionEntry.PreCompiled<?, ?>> entries, Map<GameContentType<?>, TypeIndex<?>> typeIndexMap) {
        this.entries = Objects.requireNonNull(Set.copyOf(entries));
        this.typeIndexMap = Objects.requireNonNull(Map.copyOf(typeIndexMap));
    }

    public Set<RestrictionEntry.PreCompiled<?, ?>> preCompiledRestrictions() {
        return entries;
    }

    @SuppressWarnings("unchecked")
    public <Type extends TypedGameContent> TypeIndex<Type> typeIndex(GameContentType<Type> type) {
        return (TypeIndex<Type>) typeIndexMap.getOrDefault(type, TypeIndex.EMPTY);
    }

    public Map<GameContentType<?>, TypeIndex<?>> typeIndexMap() {
        return typeIndexMap;
    }
}
