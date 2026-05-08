package de.dasbabypixel.gamestages.common.data.manager.mutable;

import de.dasbabypixel.gamestages.common.data.attribute.AttributeCompiler;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public final class ClientMutableGameStageManager extends SimpleMutableGameStageManager<ClientMutableGameStageManager, ClientGameStageManager> {
    private static @Nullable ClientMutableGameStageManager buildingInstance = null;

    public ClientGameStageManager finishBuildingInstance() {
        if (buildingInstance != this) throw new IllegalStateException();
        buildingInstance = null;
        return compile();
    }

    @Override
    public ClientGameStageManager compile(AttributeCompiler<ClientMutableGameStageManager> compiler, CompiledAttributes<ClientGameStageManager> compiledAttributes) {
        return new ClientGameStageManager(compiledAttributes.attributes());
    }

    public static ClientMutableGameStageManager beginBuildingInstance() {
        if (buildingInstance != null) throw new IllegalStateException();
        buildingInstance = new ClientMutableGameStageManager();
        return buildingInstance;
    }

    public static ClientMutableGameStageManager buildingInstance() {
        return Objects.requireNonNull(buildingInstance);
    }
}
