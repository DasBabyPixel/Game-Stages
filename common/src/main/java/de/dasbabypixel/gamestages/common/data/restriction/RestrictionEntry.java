package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryPreCompiler;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface RestrictionEntry<T extends RestrictionEntry<T, PreCompiled>, PreCompiled> {
    RestrictionEntryOrigin origin();

    T disallowDuplicates();

    T allowDuplicates();

    CustomPacket createPacket(ServerGameStageManager serverGameStageManager);

    PreCompiled precompile(AbstractGameStageManager<?> instance, RestrictionEntryPreCompiler preCompiler);

    CompiledRestrictionEntry compile(RecompilationTask task, PreCompiled preCompiled);

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
