package de.dasbabypixel.gamestages.common.data.graph;

import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.server.MutableGameStageManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class CompiledImplicitDependencyGraph {
    private final Map<DependencyContent, Compiled> compiledMap;
    private final Map<DependencyContentType, List<DependencyContent>> byType;

    public CompiledImplicitDependencyGraph(Map<DependencyContent, Compiled> compiledMap) {
        this.compiledMap = Map.copyOf(compiledMap);
        var byType = new HashMap<DependencyContentType, List<DependencyContent>>();
        for (var dependencyContent : compiledMap.keySet()) {
            byType.computeIfAbsent(dependencyContent.type(), ignored -> new ArrayList<>()).add(dependencyContent);
        }
        this.byType = Map.copyOf(byType);
    }

    public Map<DependencyContentType, List<DependencyContent>> byType() {
        return byType;
    }

    public Map<DependencyContent, Compiled> compiledMap() {
        return compiledMap;
    }

    public record Compiled(PreparedRestrictionPredicate predicate) {
    }

    public static final class Holder {
        public static final Attribute<MutableGameStageManager, Holder> ATTRIBUTE = new Attribute<>(Holder::new);
        public @Nullable CompiledImplicitDependencyGraph graph;

        public CompiledImplicitDependencyGraph graph() {
            return Objects.requireNonNull(graph);
        }
    }
}
