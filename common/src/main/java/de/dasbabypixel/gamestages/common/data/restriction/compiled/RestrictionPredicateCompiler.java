package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.logicng.LogicNG;
import de.dasbabypixel.gamestages.common.data.restriction.CompositePreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class RestrictionPredicateCompiler {
    private final BaseStages stages;
    private final Map<PreparedRestrictionPredicate, CachedCompiledRestrictionPredicate> cache = new HashMap<>();
    private final LogicNG logicNG;

    public RestrictionPredicateCompiler(BaseStages stages) {
        this.stages = stages;
        this.logicNG = stages.manager().get(LogicNG.ATTRIBUTE);
    }

    public CompiledRestrictionPredicate compile(PreparedRestrictionPredicate predicate) {
        return compile0(predicate);
    }

    private CachedCompiledRestrictionPredicate compile0(PreparedRestrictionPredicate predicate) {
        if (cache.containsKey(predicate)) return Objects.requireNonNull(cache.get(predicate));
        var dependencies = predicate instanceof CompositePreparedRestrictionPredicate composite ? composite.dependencies()
                                                                                                  .stream()
                                                                                                  .map(this::compile0)
                                                                                                  .toList() : List.<CachedCompiledRestrictionPredicate>of();
        var compiled = new CachedCompiledRestrictionPredicate(logicNG, stages, predicate, dependencies);
        dependencies.forEach(dep -> dep.addNotifier(ignored -> compiled.invalidate()));
        cache.put(predicate, compiled);
        return compiled;
    }
}
