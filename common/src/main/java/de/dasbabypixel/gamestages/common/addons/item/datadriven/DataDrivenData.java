package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.data.PlayerCompilationTask;
import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import de.dasbabypixel.gamestages.common.data.manager.mutable.AbstractMutableGameStageManager;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface DataDrivenData<P extends DataDrivenData.PreCompiled<?, C>, C extends DataDrivenData.Compiled<?>> extends CompilableResource<AbstractMutableGameStageManager<?>, P, PlayerCompilationTask, C> {
    @Override
    P precompile(AbstractMutableGameStageManager<?> manager);

    interface PreCompiled<CustomData, C extends Compiled<?>> extends CompilableResource.PreCompiled<PlayerCompilationTask, C>, ResolverAlgorithmData<CustomData, ItemStackRestrictionEntry> {
        @Override
        C compile(PlayerCompilationTask playerCompilationTask);

        @Override
        List<ItemStackRestrictionEntry> entries();
    }

    interface Compiled<CustomData> extends ResolverAlgorithmData<CustomData, CompiledItemStackRestrictionEntry> {
    }
}
