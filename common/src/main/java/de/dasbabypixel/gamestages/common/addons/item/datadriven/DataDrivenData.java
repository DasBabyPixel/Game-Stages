package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface DataDrivenData<P extends DataDrivenData.PreCompiled<?, C>, C extends DataDrivenData.Compiled<?>> extends CompilableResource<AbstractGameStageManager<?>, P, RecompilationTask, C> {
    @Override
    P precompile(AbstractGameStageManager<?> abstractGameStageManager);

    interface PreCompiled<CustomData, C extends Compiled<?>> extends CompilableResource.PreCompiled<RecompilationTask, C>, ResolverAlgorithmData<CustomData, ItemStackRestrictionEntry> {
        @Override
        C compile(RecompilationTask recompilationTask);

        @Override
        List<ItemStackRestrictionEntry> entries();
    }

    interface Compiled<CustomData> extends ResolverAlgorithmData<CustomData, CompiledItemStackRestrictionEntry> {
    }
}
