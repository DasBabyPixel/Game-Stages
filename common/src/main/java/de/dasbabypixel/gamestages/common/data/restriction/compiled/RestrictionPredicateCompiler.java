package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.restriction.CompositePreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestrictionPredicateCompiler {
    private final @NonNull Player player;
    private final Map<PreparedRestrictionPredicate, CachedCompiledRestrictionPredicate> cache = new HashMap<>();

    public RestrictionPredicateCompiler(@NonNull Player player) {
        this.player = player;
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
        var compiled = new CachedCompiledRestrictionPredicate(player, predicate.predicate(), dependencies);
        dependencies.forEach(dep -> dep.addNotifier(ignored -> compiled.invalidate()));
        cache.put(predicate, compiled);
        return compiled;
    }
}
