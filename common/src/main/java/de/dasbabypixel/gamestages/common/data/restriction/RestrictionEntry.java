package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface RestrictionEntry<T extends RestrictionEntry<T, P, C>, P extends RestrictionEntry.PreCompiled<P, C>, C extends CompiledRestrictionEntry<C, P>> extends CompilableResource<AbstractGameStageManager<?>, P, RecompilationTask, C> {
    TypedGameContent gameContent();

    RestrictionEntryOrigin origin();

    CustomPacket createPacket(ServerGameStageManager manager);

    @Override
    P precompile(AbstractGameStageManager<?> manager);

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }

    interface PreCompiled<P extends RestrictionEntry.PreCompiled<P, C>, C extends CompiledRestrictionEntry<C, P>> extends CompilableResource.PreCompiled<RecompilationTask, C> {
        RestrictionEntry<?, P, C> entry();

        TypedGameContent gameContent();

        @Override
        C compile(RecompilationTask recompilationTask);

        default RestrictionEntryOrigin origin() {
            return entry().origin();
        }
    }
}
