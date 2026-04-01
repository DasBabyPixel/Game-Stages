package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public class RestrictionEntryPreCompiler {
    public static final AbstractGameStageManager.Attribute<RestrictionEntryPreCompiler> ATTRIBUTE = new AbstractGameStageManager.Attribute<>(RestrictionEntryPreCompiler::new);
    private final @NonNull Map<@NonNull RestrictionEntry<?, ?>, Object> preCompiledCache = new HashMap<>();
    private final @NonNull AbstractGameStageManager instance;

    private RestrictionEntryPreCompiler(@NonNull AbstractGameStageManager instance) {
        this.instance = instance;
    }

    public @NonNull Map<@NonNull RestrictionEntry<?, ?>, Object> preCompiledCache() {
        return preCompiledCache;
    }

    public void precompile(@NonNull RestrictionEntry<?, ?> entry) {
        var precompiled = entry.precompile(instance, this);
        preCompiledCache.put(entry, precompiled);
    }
}
