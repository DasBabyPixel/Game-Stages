package de.dasbabypixel.gamestages.common.data.flattening;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import org.jspecify.annotations.NonNull;

public interface GameContentFlattener {
    @NonNull FlattenedGameContent flatten(@NonNull GameContent gameContent);

    <T extends TypedGameContent> @NonNull T flatten(@NonNull GameContent gameContent, @NonNull GameContentType<T> requestType);

    interface FlattenerFactory<T extends TypedGameContent> {
        @NonNull GameContentType<T> type();

        @NonNull Flattener<T> createUnion();

        @NonNull Flattener<T> createOnly();

        @NonNull Flattener<T> createExcept();
    }

    interface Flattener<T extends TypedGameContent> {
        void accept(@NonNull T list);

        @NonNull T complete();
    }

    class Attribute {
        public static AbstractGameStageManager.Attribute<GameContentFlattener> INSTANCE;
    }
}
