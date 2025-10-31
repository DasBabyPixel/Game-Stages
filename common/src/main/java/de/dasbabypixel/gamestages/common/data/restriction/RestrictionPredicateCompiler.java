package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
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
        var dependencies = predicate.dependencies().stream().map(this::compile0).toList();
        var compiled = new CachedCompiledRestrictionPredicate(player, predicate.predicate(), dependencies);
        cache.put(predicate, compiled);
        return compiled;
    }
}
