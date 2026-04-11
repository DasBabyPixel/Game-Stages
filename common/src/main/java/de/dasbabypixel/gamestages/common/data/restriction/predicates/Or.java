package de.dasbabypixel.gamestages.common.data.restriction.predicates;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.restriction.CompositePreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public final class Or implements RestrictionPredicate {
    public static final Or INSTANCE = new Or();

    private Or() {
    }

    @SuppressWarnings({"StatementWithEmptyBody", "ForLoopReplaceableByForEach"})
    @Override
    public List<PreparedRestrictionPredicate> optimize(List<PreparedRestrictionPredicate> list) {
        var canOptimize = false;
        for (var i = 0; i < list.size(); i++) {
            var p = list.get(i);
            if (p.predicate() instanceof True) {
                return List.of(p);
            } else if (p.predicate() instanceof False) {
                canOptimize = true;
                break;
            }
        }
        if (canOptimize) {
            var newList = new ArrayList<PreparedRestrictionPredicate>();
            for (var i = 0; i < list.size(); i++) {
                var p = list.get(i);
                if (p.predicate() instanceof True) {
                    return List.of((p));
                } else if (p.predicate() instanceof False) {
                    // ignore
                } else {
                    newList.add(p);
                }
            }
            return newList;
        }
        return list;
    }

    @Override
    public PreparedRestrictionPredicate prepare(List<PreparedRestrictionPredicate> dependencies) {
        dependencies = optimize(dependencies);
        if (dependencies.isEmpty()) return False.INSTANCE.prepare();
        var f = dependencies.getFirst();
        if (f.predicate() instanceof True) return True.INSTANCE.prepare();
        return new CompositePreparedRestrictionPredicate(this, dependencies);
    }

    @Override
    public boolean accepts(List<? extends PreparedRestrictionPredicate> dependencies) {
        return true;
    }

    @Override
    public boolean test(List<? extends CompiledRestrictionPredicate> dependencies, BaseStages stages) {
        for (var restriction : dependencies) {
            if (restriction.test()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "||";
    }
}
