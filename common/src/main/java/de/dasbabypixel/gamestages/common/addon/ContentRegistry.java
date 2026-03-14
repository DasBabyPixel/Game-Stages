package de.dasbabypixel.gamestages.common.addon;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;

import java.lang.reflect.Type;

public interface ContentRegistry {
    Attribute<String> NAME = Attribute.create(String.class);
    Attribute<GameContentFlattener.FlattenerFactory<?>> FLATTENER_FACTORY = Attribute.create(GameContentFlattener.FlattenerFactory.class);

    <T extends TypedGameContent> Builder<T> prepare(GameContentType<T> type);

    interface Builder<T extends TypedGameContent> {
        <V> Builder<T> set(Attribute<V> attribute, V value);

        void register();
    }

    final class Attribute<V> {
        private static int idCounter;
        private final int id;
        private final Type type;

        private Attribute(int id, Type type) {
            this.id = id;
            this.type = type;
        }

        public static <V> Attribute<V> create(Type type) {
            return new Attribute<>(idCounter++, type);
        }

        public Type type() {
            return type;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Attribute<?> a && a.id == id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
}
