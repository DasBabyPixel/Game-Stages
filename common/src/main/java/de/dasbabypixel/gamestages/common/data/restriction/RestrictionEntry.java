package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.data.PlayerCompilationTask;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.AbstractMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface RestrictionEntry<T extends RestrictionEntry<T, P, C>, P extends RestrictionEntry.PreCompiled<P, C>, C extends CompiledRestrictionEntry<C, P>> extends CompilableResource<AbstractMutableGameStageManager<?>, P> {
    TypedGameContent gameContent();

    RestrictionEntryOrigin origin();

    @Override
    P compile(AbstractMutableGameStageManager<?> manager);

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }

    interface PreCompiled<P extends RestrictionEntry.PreCompiled<P, C>, C extends CompiledRestrictionEntry<C, P>> extends CompilableResource<PlayerCompilationTask, C> {
        RestrictionEntry<?, P, C> entry();

        TypedGameContent gameContent();

        CustomPacket createPacket(ServerGameStageManager manager);

        @Override
        C compile(PlayerCompilationTask playerCompilationTask);

        default RestrictionEntryOrigin origin() {
            return entry().origin();
        }
    }
}
