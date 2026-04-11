package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class RestrictionEntryPreCompiler {
    public static final Attribute<AbstractGameStageManager<?>, RestrictionEntryPreCompiler> ATTRIBUTE = new Attribute<>(RestrictionEntryPreCompiler::new);
    private final Map<RestrictionEntry<?, ?>, Object> preCompiledCache = new HashMap<>();
    private final AbstractGameStageManager<?> instance;

    private RestrictionEntryPreCompiler(AbstractGameStageManager<?> instance) {
        this.instance = instance;
    }

    public Map<RestrictionEntry<?, ?>, Object> preCompiledCache() {
        return preCompiledCache;
    }

    public void precompile(RestrictionEntry<?, ?> entry) {
        var precompiled = entry.precompile(instance, this);
        preCompiledCache.put(entry, precompiled);
    }
}
