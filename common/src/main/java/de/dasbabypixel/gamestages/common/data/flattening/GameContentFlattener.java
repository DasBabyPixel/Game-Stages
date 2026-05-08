package de.dasbabypixel.gamestages.common.data.flattening;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.attribute.CompilableAttribute;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.SimpleMutableGameStageManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

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

    class Attribute {
        public static final CompilableAttribute<SimpleMutableGameStageManager<?, ?>, GameContentFlattener, AbstractGameStageManager<?>> MUTABLE_MANAGER_ATTRIBUTE = CompilableAttribute.noop();

        public static class Factory {
            public static @Nullable Supplier<GameContentFlattener> FACTORY;
        }
    }
}
