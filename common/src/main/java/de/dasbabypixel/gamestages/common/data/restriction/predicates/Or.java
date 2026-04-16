package de.dasbabypixel.gamestages.common.data.restriction.predicates;

import de.dasbabypixel.gamestages.common.data.restriction.CompositePreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import org.jspecify.annotations.NullMarked;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@NullMarked
public final class Or implements RestrictionPredicate {
    public static final Or INSTANCE = new Or();

    private Or() {
    }

    @SuppressWarnings({"StatementWithEmptyBody", "ForLoopReplaceableByForEach"})
    @Override
    public List<PreparedRestrictionPredicate> optimize(List<PreparedRestrictionPredicate> dependencies) {
        var newList = new HashSet<PreparedRestrictionPredicate>();
        for (var i = 0; i < dependencies.size(); i++) {
            var p = dependencies.get(i);
            if (p.predicate() instanceof False) {
                // ignore
            } else if (p.predicate() instanceof Or) {
                newList.addAll(((CompositePreparedRestrictionPredicate) p).dependencies());
            } else {
                newList.add(p);
            }
        }
        if (newList.contains(True.PREPARED)) return List.of(True.PREPARED);
        Set<PreparedRestrictionPredicate> toRemove = new HashSet<>();
        for (var outer : newList) {
            for (var target : newList) {
                if (target.predicate() instanceof And) {
                    var deps = ((CompositePreparedRestrictionPredicate) target).dependencies();
                    if (deps.contains(outer)) {
                        toRemove.add(target);
                    } else {
                        for (var dep : deps) {
                            if (dep.predicate() instanceof Or) {
                                var orDeps = ((CompositePreparedRestrictionPredicate) dep).dependencies();
                                if (newList.containsAll(orDeps)) toRemove.add(target);
                                break;
                            }
                        }
                    }
                }
            }
        }
        newList.removeAll(toRemove);
        return Objects.requireNonNull(List.copyOf(newList));
    }

    @Override
    public Formula convertToLogicNG(FormulaFactory factory, Formula[] dependencies) {
        return Objects.requireNonNull(factory.or(dependencies));
    }

    @Override
    public boolean equals(List<PreparedRestrictionPredicate> dependencies1, List<PreparedRestrictionPredicate> dependencies2) {
        return Set.copyOf(dependencies1).equals(Set.copyOf(dependencies2));
    }

    @Override
    public int hash(List<PreparedRestrictionPredicate> dependencies) {
        return Objects.hash(this, Set.copyOf(dependencies));
    }

    @Override
    public int hashCode() {
        return 0x4b5f019b;
    }

    @Override
    public PreparedRestrictionPredicate prepare(List<PreparedRestrictionPredicate> dependencies) {
        dependencies = optimize(dependencies);
        if (dependencies.isEmpty()) return False.INSTANCE.prepare();
        if (dependencies.size() == 1) return dependencies.getFirst();
        return new CompositePreparedRestrictionPredicate(this, dependencies);
    }

    @Override
    public boolean accepts(List<? extends PreparedRestrictionPredicate> dependencies) {
        return true;
    }

    @Override
    public String toString() {
        return "||";
    }
}
