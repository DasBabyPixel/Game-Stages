package de.dasbabypixel.gamestages.common.data.restriction.predicates;

import de.dasbabypixel.gamestages.common.data.restriction.CompositePreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class And implements RestrictionPredicate {
    public static final And INSTANCE = new And();

    private And() {
    }

    @SuppressWarnings({"StatementWithEmptyBody", "ForLoopReplaceableByForEach"})
    @Override
    public List<PreparedRestrictionPredicate> optimize(List<PreparedRestrictionPredicate> list) {
        var canOptimize = false;
        for (var i = 0; i < list.size(); i++) {
            var p = list.get(i);
            if (p.predicate() instanceof True) {
                canOptimize = true;
                break;
            } else if (p.predicate() instanceof False) {
                return List.of(p);
            }
        }
        if (canOptimize) {
            var newList = new ArrayList<PreparedRestrictionPredicate>();
            for (var i = 0; i < list.size(); i++) {
                var p = list.get(i);
                if (p.predicate() instanceof True) {
                    // ignore
                } else if (p.predicate() instanceof False) {
                    return List.of((p));
                } else {
                    newList.add(p);
                }
            }
            return newList;
        }
        return list;
    }

    @Override
    public @NonNull PreparedRestrictionPredicate prepare(@NonNull List<PreparedRestrictionPredicate> dependencies) {
        dependencies = optimize(dependencies);
        if (dependencies.isEmpty()) return True.INSTANCE.prepare();
        var f = dependencies.getFirst();
        if (f.predicate() instanceof False) return False.INSTANCE.prepare();
        return new CompositePreparedRestrictionPredicate(this, dependencies);
    }

    @Override
    public boolean accepts(@NonNull List<? extends @NonNull PreparedRestrictionPredicate> dependencies) {
        return true;
    }

    @Override
    public boolean test(@NonNull List<? extends CompiledRestrictionPredicate> dependencies, @NonNull Player player) {
        for (var restriction : dependencies) {
            if (!restriction.test()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NonNull String toString() {
        return "&&";
    }
}
