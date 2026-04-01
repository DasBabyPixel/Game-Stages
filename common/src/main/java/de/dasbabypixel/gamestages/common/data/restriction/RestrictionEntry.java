package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryPreCompiler;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import org.jspecify.annotations.NonNull;

public interface RestrictionEntry<T extends RestrictionEntry<T, PreCompiled>, PreCompiled> {
    @NonNull RestrictionEntryOrigin origin();

    @NonNull T disallowDuplicates();

    @NonNull T allowDuplicates();

    @NonNull CustomPacket createPacket(@NonNull ServerGameStageManager serverGameStageManager);

    @NonNull PreCompiled precompile(@NonNull AbstractGameStageManager instance, @NonNull RestrictionEntryPreCompiler preCompiler);

    @NonNull CompiledRestrictionEntry compile(@NonNull RecompilationTask task, @NonNull PreCompiled preCompiled);

    @SuppressWarnings("unchecked")
    default @NonNull T self() {
        return (T) this;
    }
}
