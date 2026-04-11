package de.dasbabypixel.gamestages.common.addon;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Type;

@NullMarked
public interface ContentRegistry {
    Attribute NAME = Attribute.create(String.class);
    Attribute FLATTENER_FACTORY = Attribute.create(GameContentFlattener.FlattenerFactory.class);

    <T extends TypedGameContent> Builder<T> prepare(GameContentType<T> type);

    interface Builder<T extends TypedGameContent> {
        <V> Builder<T> set(Attribute attribute, V value);

        void register();
    }

    final class Attribute {
        private static int idCounter;
        private final int id;
        private final Type type;

        private Attribute(int id, Type type) {
            this.id = id;
            this.type = type;
        }

        public Type type() {
            return type;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Attribute a && a.id == id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        public static <V> Attribute create(Type type) {
            return new Attribute(idCounter++, type);
        }
    }
}
