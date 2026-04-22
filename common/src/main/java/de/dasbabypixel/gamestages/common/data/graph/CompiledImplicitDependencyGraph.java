package de.dasbabypixel.gamestages.common.data.graph;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@NullMarked
public class CompiledImplicitDependencyGraph {
    //    public static final AttributeQuery.Holder<ServerMutableGameStageManager, CompiledImplicitDependencyGraph> ATTRIBUTE = AttributeQuery.holder();
    private final Map<DependencyContent, Compiled> compiledMap;
    private final Map<DependencyContentType, List<DependencyContent>> byType;

    public CompiledImplicitDependencyGraph(Map<DependencyContent, Compiled> compiledMap) {
        this.compiledMap = Objects.requireNonNull(Map.copyOf(compiledMap));
        var byType = new HashMap<DependencyContentType, List<DependencyContent>>();
        for (var dependencyContent : compiledMap.keySet()) {
            byType.computeIfAbsent(dependencyContent.type(), ignored -> new ArrayList<>()).add(dependencyContent);
        }
        this.byType = Objects.requireNonNull(Map.copyOf(byType));
    }

    public Map<DependencyContentType, List<DependencyContent>> byType() {
        return byType;
    }

    public Map<DependencyContent, Compiled> compiledMap() {
        return compiledMap;
    }

    public record Compiled(PreparedRestrictionPredicate predicate, Set<DependencyContent> requiredOrigins,
                           Set<DependencyContent> possibleOrigins) {
        public Compiled {
            possibleOrigins = Objects.requireNonNull(Set.copyOf(possibleOrigins));
            requiredOrigins = Objects.requireNonNull(Set.copyOf(requiredOrigins));
        }
    }
}
