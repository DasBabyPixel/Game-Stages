package de.dasbabypixel.gamestages.common.data.graph;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.And;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.False;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.Not;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.Or;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.True;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.transformations.simplification.AdvancedSimplifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A graph for ordering implicit dependencies and handling transitive dependencies correctly.
 * <p>
 * An implicit dependency can be described as a possible way to obtain some possibly new content
 */
@NullMarked
public class ImplicitDependencyGraph {
    private final Map<DependencyContent, Node> nodeMap = new HashMap<>();

    public void register(DependencyContent content) {
        if (nodeMap.containsKey(content)) return;
        nodeMap.put(content, new Node(content));
    }

    public void restrict(DependencyContent content, PreparedRestrictionPredicate predicate) {
        var node = Objects.requireNonNull(nodeMap.get(content), "Content not registered");
        node.explicit(predicate);
    }

    public void addOutput(DependencyContent recipe, DependencyContent produced) {
        var node = Objects.requireNonNull(nodeMap.get(recipe), "Recipe not registered");
        node.addOutput(Objects.requireNonNull(nodeMap.get(produced), "Produced not registered"));
    }

    public void addOrigin(DependencyContent content, DependencyContent origin) {
        var node = Objects.requireNonNull(nodeMap.get(content), "Content not registered");
        node.addParent(Objects.requireNonNull(nodeMap.get(origin), "Origin not registered"));
    }

    public CompiledImplicitDependencyGraph compile() {
        var compiler = new Compiler();
        compiler.computeFinalConditions(List.copyOf(nodeMap.values()));
        var compiledMap = new HashMap<DependencyContent, CompiledImplicitDependencyGraph.Compiled>();
        for (var node : nodeMap.values()) {
            var pred = convert(Objects.requireNonNull(node.finalFormula));
            var compiled = new CompiledImplicitDependencyGraph.Compiled(pred);
            compiledMap.put(node.content, compiled);
        }
        return new CompiledImplicitDependencyGraph(compiledMap);
    }

    @SuppressWarnings("DataFlowIssue")
    private static PreparedRestrictionPredicate convert(Formula formula) {
        return switch (Objects.requireNonNull(formula.type())) {
            case PBC, EQUIV, IMPL -> throw new UnsupportedOperationException();
            case OR -> Or.INSTANCE.prepare(formula.stream().map(ImplicitDependencyGraph::convert).toList());
            case AND -> And.INSTANCE.prepare(formula.stream().map(ImplicitDependencyGraph::convert).toList());
            case NOT -> Not.INSTANCE.prepare(convert(formula.iterator().next()));
            case LITERAL -> {
                var lit = (Literal) formula;
                if (lit.phase()) {
                    yield new GameStage(lit.name());
                } else {
                    yield Not.INSTANCE.prepare(new GameStage(lit.name()));
                }
            }
            case TRUE -> True.PREPARED;
            case FALSE -> False.PREPARED;
        };
    }

    private static class SingleState {
        private final Node target;
        private final Map<Node, Formula> cache = new HashMap<>();
        private final Set<Node> computing = new HashSet<>();

        public SingleState(Node target) {
            this.target = target;
        }
    }

    private static class Node {
        private final DependencyContent content;
        /**
         * The list of nodes can directly produce this node.
         * Unlocking any of these origins means that the "origin" condition is met.
         */
        private final Set<Node> possibleOrigins = new HashSet<>();
        /**
         * A list of required nodes to unlock this node.
         * If the node is locked, then this node is also locked.
         */
        private final Set<Node> requiredOrigins = new HashSet<>();
        private @Nullable Formula finalFormula = null;
        /**
         * Explicit predicate, directly specified by config
         */
        private PreparedRestrictionPredicate explicitPredicate = True.PREPARED;
        private @Nullable Formula explicitPredicateFormula = null;

        public Node(DependencyContent content) {
            this.content = content;
        }

        public Node explicit(PreparedRestrictionPredicate predicate) {
            this.explicitPredicate = predicate;
            return this;
        }

        public Node addParent(Node parent) {
            possibleOrigins.add(parent);
            return this;
        }

        public Node addOutput(Node output) {
            if (!output.requiredOrigins.isEmpty()) throw new IllegalStateException();
            requiredOrigins.add(output);
            output.addParent(this);
            return this;
        }

        @Override
        public String toString() {
            return content.toString();
        }
    }

    private static final class Compiler {
        private final FormulaFactory factory = new FormulaFactory();
        private final AdvancedSimplifier simplifier = new AdvancedSimplifier();

        private void computeFinalConditions(List<Node> nodes) {
            for (var node : nodes) {
                node.explicitPredicateFormula = simplifier.apply(node.explicitPredicate.convertToLogicNG(factory), true);
            }
            for (var node : nodes) {
                compute(new SingleState(node));
            }

            System.out.println("Final");
            System.out.println("Final");
            for (var node : nodes) {
                var formula = Objects.requireNonNull(node.finalFormula);
                formula = simplifier.apply(formula, true);
                System.out.println(node + " -> " + formula);
            }
        }

        private @Nullable Formula compute(SingleState state, Node target) {
            if (state.cache.containsKey(target)) return Objects.requireNonNull(state.cache.get(target));
            if (state.computing.contains(target)) return null;
            state.computing.add(target);
            var allRequired = new ArrayList<Formula>();
            allRequired.add(Objects.requireNonNull(target.explicitPredicateFormula));
            for (var requiredOrigin : target.requiredOrigins) {
                allRequired.add(Objects.requireNonNull(requiredOrigin.explicitPredicateFormula));
            }
            var anyOfDependsOnTarget = false;
            var requireAnyOf = new ArrayList<Formula>();
            for (var possibleOrigin : target.possibleOrigins) {
                var cond = compute(state, possibleOrigin);
                if (cond != null) requireAnyOf.add(cond);
                else anyOfDependsOnTarget = true;
            }

            var requiredCondition = factory.and(allRequired);
            var anyOfCondition = requireAnyOf.isEmpty() ? (anyOfDependsOnTarget ? factory.falsum() : factory.verum()) : factory.or(requireAnyOf);

            var cond = Objects.requireNonNull(factory.and(requiredCondition, anyOfCondition));
            state.cache.put(target, cond);
            return cond;
        }

        private void compute(SingleState state) {
            state.target.finalFormula = Objects.requireNonNull(compute(state, state.target));
        }
    }
}
