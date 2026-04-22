package de.dasbabypixel.gamestages.common.data.manager.mutable;

import de.dasbabypixel.gamestages.common.data.logicng.LogicNG;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.compiler.ManagerCompilerTask;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public final class ClientMutableGameStageManager extends AbstractMutableGameStageManager<ClientMutableGameStageManager> {
    private static @Nullable ClientMutableGameStageManager buildingInstance = null;

    public ClientGameStageManager finishBuildingInstance() {
        if (buildingInstance != this) throw new IllegalStateException();
        buildingInstance = null;
        var compilerTask = new ManagerCompilerTask(this);
        compilerTask.precompileRestrictions();
        var restrictions = compilerTask.preCompileIndex().preCompiledRestrictions();
        var immutable = new ClientGameStageManager(gameStages, restrictions);
        LogicNG.ATTRIBUTE.init(immutable, this.get(LogicNG.ATTRIBUTE));
        return compilerTask.postCompile(immutable);
    }

    public static ClientMutableGameStageManager beginBuildingInstance() {
        if (buildingInstance != null) throw new IllegalStateException();
        buildingInstance = new ClientMutableGameStageManager();
        LogicNG.ATTRIBUTE.init(buildingInstance, new LogicNG());
        return buildingInstance;
    }

    public static ClientMutableGameStageManager buildingInstance() {
        return Objects.requireNonNull(buildingInstance);
    }
}
