package de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.*;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@NullMarked
public class SequentialCompiler implements DirectCompiler<SequentialData> {
    @Override
    public CompiledResolverAlgorithm compile(DataDrivenCompiler compiler, SequentialData data, DataDrivenResolverFactory.Context context) {
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
