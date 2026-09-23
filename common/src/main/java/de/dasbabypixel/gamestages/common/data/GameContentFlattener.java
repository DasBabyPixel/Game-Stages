package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.attribute.CompilableAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleAttribute;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.SimpleMutableGameStageManager;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface GameContentFlattener {
    CompilableAttribute<SimpleMutableGameStageManager<?, ?>, GameContentFlattener, AbstractGameStageManager<?>> MUTABLE_MANAGER_ATTRIBUTE = CompilableAttribute.noop();
    SimpleAttribute<PlayerCompilationTask, GameContentFlattener> PLAYER_COMPILATION_ATTRIBUTE = new SimpleAttribute<>();

    GameContentSimple flatten(GameContentWrapper content);

    <TypeData, Elements, Element> GameContentDirect<TypeData, Elements, Element> flatten(GameContentWrapper content, GameContentRegistry.Entry<?, TypeData, Elements, Element> type);
}
