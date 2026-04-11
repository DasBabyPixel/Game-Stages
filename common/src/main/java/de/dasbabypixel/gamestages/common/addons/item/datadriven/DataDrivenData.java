package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface DataDrivenData<PreCompiled extends DataDrivenData.PreCompiled<Compiled>, Compiled extends CompiledResolverAlgorithm> extends CompilableResource<AbstractGameStageManager<?>, PreCompiled, RecompilationTask, Compiled> {
    @Override
    PreCompiled precompile(AbstractGameStageManager<?> abstractGameStageManager);

    interface PreCompiled<Compiled extends CompiledResolverAlgorithm> extends CompilableResource.PreCompiled<RecompilationTask, Compiled> {
        @Override
        Compiled compile(RecompilationTask recompilationTask);
    }
}
