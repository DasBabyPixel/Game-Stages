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
    public PreparedRestrictionPredicate prepare(List<PreparedRestrictionPredicate> dependencies) {
        dependencies = optimize(dependencies);
        if (dependencies.isEmpty()) return True.INSTANCE.prepare();
        var f = dependencies.getFirst();
        if (f.predicate() instanceof False) return False.INSTANCE.prepare();
        return new CompositePreparedRestrictionPredicate(this, dependencies);
    }

    @Override
    public boolean accepts(List<? extends PreparedRestrictionPredicate> dependencies) {
        return true;
    }

    @Override
    public boolean test(List<? extends CompiledRestrictionPredicate> dependencies, BaseStages stages) {
        for (var restriction : dependencies) {
            if (!restriction.test()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "&&";
    }
}
