package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.restriction.types.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public class RestrictionEntryCompiler {
    public static final ServerGameStageManager.Attribute<RestrictionEntryCompiler> ATTRIBUTE = new ServerGameStageManager.Attribute<>(RestrictionEntryCompiler::new);
    private final ServerGameStageManager instance;
    private final Map<RestrictionEntry<?, ?>, Object> preCompiledCache = new HashMap<>();

    private RestrictionEntryCompiler(ServerGameStageManager instance) {
        this.instance = instance;
    }

    public void precompile(@NonNull RestrictionEntry<?, ?> entry) {
        var precompiled = entry.precompile(instance);
        preCompiledCache.put(entry, precompiled);
    }

    @SuppressWarnings("unchecked")
    public @NonNull CompiledRestrictionEntry compile(@NonNull Player player, @NonNull RestrictionEntry<?, ?> entry, @NonNull CompiledRestrictionPredicate predicate) {
        var preCompiled = preCompiledCache.get(entry);
        return ((RestrictionEntry<?, Object>) entry).compile(instance, preCompiled, predicate);
    }
}
