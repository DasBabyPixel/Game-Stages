package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.types.RestrictionEntry;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RestrictionEntryCompiler {
    public static final AbstractGameStageManager.Attribute<RestrictionEntryCompiler> ATTRIBUTE = new AbstractGameStageManager.Attribute<>(RestrictionEntryCompiler::new);
    private final AbstractGameStageManager instance;
    private final Map<RestrictionEntry<?, ?>, Object> preCompiledCache = new HashMap<>();

    private RestrictionEntryCompiler(AbstractGameStageManager instance) {
        this.instance = instance;
    }

    public AbstractGameStageManager instance() {
        return instance;
    }

    public void precompile(@NonNull RestrictionEntry<?, ?> entry) {
        var precompiled = entry.precompile(instance);
        preCompiledCache.put(entry, precompiled);
    }

    @SuppressWarnings("unchecked")
    public @NonNull CompiledRestrictionEntry compile(@NonNull RestrictionEntry<?, ?> entry, @NonNull CompiledRestrictionPredicate predicate) {
        var preCompiled = Objects.requireNonNull(preCompiledCache.get(entry));
        return ((RestrictionEntry<?, Object>) entry).compile(instance, preCompiled, predicate);
    }
}
