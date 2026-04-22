package de.dasbabypixel.gamestages.common.data.flattening;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.attribute.AttributeHolder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface GameContentFlattener {
    FlattenedGameContent flatten(GameContent gameContent);

    <T extends TypedGameContent> T flatten(GameContent gameContent, GameContentType<T> requestType);

    interface FlattenerFactory<T extends TypedGameContent> {
        GameContentType<T> type();

        Flattener<T> createUnion();

        Flattener<T> createOnly();

        Flattener<T> createExcept();
    }

    interface Flattener<T extends TypedGameContent> {
        void accept(T list);

        T complete();
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    class Attribute {
        public static de.dasbabypixel.gamestages.common.data.attribute.Attribute<AttributeHolder<?>, GameContentFlattener> INSTANCE;
    }
}
