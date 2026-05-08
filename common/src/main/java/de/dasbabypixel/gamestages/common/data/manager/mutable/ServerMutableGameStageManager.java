package de.dasbabypixel.gamestages.common.data.manager.mutable;

import de.dasbabypixel.gamestages.common.data.attribute.AttributeCompiler;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ServerMutableGameStageManager extends SimpleMutableGameStageManager<ServerMutableGameStageManager, ServerGameStageManager> {
    @Override
    public ServerGameStageManager compile(AttributeCompiler<ServerMutableGameStageManager> compiler, CompiledAttributes<ServerGameStageManager> compiledAttributes) {
        return new ServerGameStageManager(compiledAttributes.attributes());
    }
}
