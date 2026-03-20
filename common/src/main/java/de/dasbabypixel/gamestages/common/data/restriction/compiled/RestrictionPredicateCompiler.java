package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.restriction.CompositePreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestrictionPredicateCompiler {
    private final @NonNull BaseStages stages;
    private final Map<PreparedRestrictionPredicate, CachedCompiledRestrictionPredicate> cache = new HashMap<>();

    public RestrictionPredicateCompiler(@NonNull BaseStages stages) {
        this.stages = stages;
    }

    public @NonNull CompiledRestrictionPredicate compile(@NonNull PreparedRestrictionPredicate predicate) {
        return compile0(predicate);
    }

    private @NonNull CachedCompiledRestrictionPredicate compile0(@NonNull PreparedRestrictionPredicate predicate) {
        if (cache.containsKey(predicate)) return cache.get(predicate);
        var dependencies = predicate instanceof CompositePreparedRestrictionPredicate composite ? composite
                .dependencies()
                .stream()
                .map(this::compile0)
                .toList() : List.<CachedCompiledRestrictionPredicate>of();
        var compiled = new CachedCompiledRestrictionPredicate(stages, predicate, predicate.predicate(), dependencies);
        dependencies.forEach(dep -> dep.addNotifier(ignored -> compiled.invalidate()));
        cache.put(predicate, compiled);
        return compiled;
    }
}
