package de.dasbabypixel.gamestages.common.data.restriction.types;

import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import org.jspecify.annotations.NonNull;

public interface RestrictionEntry<T extends RestrictionEntry<T, PreCompiled>, PreCompiled> {
    @NonNull
    PreparedRestrictionPredicate predicate();

    @NonNull
    T disallowDuplicates();

    @NonNull
    T allowDuplicates();

    @NonNull
    CustomPacket createPacket(@NonNull ServerGameStageManager serverGameStageManager);

    @NonNull
    PreCompiled precompile(@NonNull ServerGameStageManager instance);

    @NonNull
    CompiledRestrictionEntry compile(@NonNull ServerGameStageManager instance, @NonNull PreCompiled preCompiled, @NonNull CompiledRestrictionPredicate predicate);

    @SuppressWarnings("unchecked")
    default @NonNull T self() {
        return (T) this;
    }
}
