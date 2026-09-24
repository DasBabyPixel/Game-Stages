package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public final class GameContentSimple implements GameContent {
    private final Map<GameContentRegistry.Entry<?, ?, ?, ?>, Object> content;

    public GameContentSimple(List<TypeEntry<?>> entries) {
        var map = new HashMap<GameContentRegistry.Entry<?, ?, ?, ?>, Object>();
        for (var e : entries) {
            map.put(e.typeEntry(), e.elements());
        }
        this.content = Map.copyOf(map);
    }

    @Override
    public <TypeData, Elements, Element> TypedGameContent<TypeData, Elements, Element> filterType(GameContentRegistry.Entry<?, TypeData, Elements, Element> type) {
        var content = content(type);
        if (content == null) return type.empty();
        return GameContentDirect.create(type, content);
    }

    @SuppressWarnings("unchecked")
    public <Elements> @Nullable Elements content(GameContentRegistry.Entry<?, ?, Elements, ?> type) {
        return (Elements) content.get(type);
    }

    @SuppressWarnings({"unchecked"})
    public List<TypeEntry<?>> entries() {
        var l = new ArrayList<TypeEntry<?>>();
        for (var e : content.entrySet()) {
            Objects.requireNonNull(e);
            l.add(new TypeEntry<>((GameContentRegistry.Entry<?, ?, @NonNull Object, ?>) e.getKey(), e.getValue()));
        }
        return List.copyOf(l);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("simple(");
        boolean first = true;
        for (var entry : content.entrySet()) {
            Objects.requireNonNull(entry);
            if (!first) {
                sb.append(", ");
            }
            var elementsString = ((GameContentType<?, @NonNull Object, ?>) entry
                    .getKey()
                    .type()).toStringElements(entry.getValue());
            sb.append(entry.getKey().id()).append('=').append(elementsString);
        }
        sb.append(")");
        return sb.toString();
    }

    @NullMarked
    public record TypeEntry<Elements>(GameContentRegistry.Entry<?, ?, Elements, ?> typeEntry, Elements elements) {
        private static <TypeData, Elements, Element> GameContentDirect<TypeData, Elements, Element> direct(GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry, Elements elements) {
            return GameContentDirect.create(typeEntry, elements);
        }

        public Collection<? extends Object> contentCollection() {
            return typeEntry.type().elementCollection(elements);
        }

        public GameContentDirect<?, Elements, ?> direct() {
            return direct(typeEntry, elements);
        }
    }
}
