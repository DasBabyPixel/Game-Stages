package de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.*;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class SequentialCompiler implements DirectCompiler<SequentialData> {
    @Override
    public @NonNull CompiledResolverAlgorithm compile(@NonNull DataDrivenCompiler compiler, @NonNull SequentialData data, DataDrivenResolverFactory.@NonNull Context context) {
        var entries = new HashSet<CompiledItemStackRestrictionEntry>();
        var list = new ArrayList<CompiledResolverAlgorithm>();
        for (var value : data.values()) {
            var compiled = compiler.compile(value, context);
            list.add(compiled);
            entries.addAll(compiled.entries());
        }
        return new SequentialCompiled(list, Objects.requireNonNull(List.copyOf(entries)));
    }
}
