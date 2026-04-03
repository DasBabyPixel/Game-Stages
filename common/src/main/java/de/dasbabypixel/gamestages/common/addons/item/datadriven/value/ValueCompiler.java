package de.dasbabypixel.gamestages.common.addons.item.datadriven.value;

import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledResolverAlgorithm;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenCompiler;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenResolverFactory;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DirectCompiler;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

public class ValueCompiler implements DirectCompiler<@NonNull ValueData> {
    @Override
    public @NonNull CompiledResolverAlgorithm compile(@NonNull DataDrivenCompiler compiler, @NonNull ValueData data, DataDrivenResolverFactory.@NonNull Context context) {
        var ctx = ((ItemAddon.RecompileContext) context.task().getContext(ItemAddon.instance())).compilationContext();
        var entry = Objects.requireNonNull(ctx.compiledMap.get(data.restrictionEntryReference()));

        var entries = List.of(entry);
        return new ValueCompiled(entry, entries);
    }
}
