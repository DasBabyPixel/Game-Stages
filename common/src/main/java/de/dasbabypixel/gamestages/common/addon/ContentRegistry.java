package de.dasbabypixel.gamestages.common.addon;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Type;

public interface ContentRegistry {
    @NonNull Attribute NAME = Attribute.create(String.class);
    @NonNull Attribute FLATTENER_FACTORY = Attribute.create(GameContentFlattener.FlattenerFactory.class);

    <T extends TypedGameContent> @NonNull Builder<T> prepare(GameContentType<T> type);

    interface Builder<T extends TypedGameContent> {
        <V> @NonNull Builder<T> set(@NonNull Attribute attribute, @NonNull V value);

        void register();
    }

    final class Attribute {
        private static int idCounter;
        private final int id;
        private final @NonNull Type type;

        private Attribute(int id, @NonNull Type type) {
            this.id = id;
            this.type = type;
        }

        public @NonNull Type type() {
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

        public static <V> @NonNull Attribute create(@NonNull Type type) {
            return new Attribute(idCounter++, type);
        }
    }
}
