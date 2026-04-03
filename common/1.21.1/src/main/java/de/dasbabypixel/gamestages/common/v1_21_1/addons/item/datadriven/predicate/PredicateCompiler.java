package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.predicate;

import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledResolverAlgorithm;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenCompiler;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenResolverFactory;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DirectCompiler;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class PredicateCompiler implements DirectCompiler<PredicateData> {
    @Override
    public @NonNull CompiledResolverAlgorithm compile(@NonNull DataDrivenCompiler compiler, @NonNull PredicateData data, DataDrivenResolverFactory.@NonNull Context context) {
        var ctx = ((ItemAddon.RecompileContext) context.task().getContext(ItemAddon.instance())).compilationContext();
        var entry = Objects.requireNonNull(ctx.compiledMap.get(data.resultReference()));
        return new PredicateCompiled(entry, data.predicate());
    }
}
