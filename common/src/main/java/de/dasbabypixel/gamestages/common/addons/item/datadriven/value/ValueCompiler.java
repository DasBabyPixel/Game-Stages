package de.dasbabypixel.gamestages.common.addons.item.datadriven.value;

import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledResolverAlgorithm;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenCompiler;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenResolverFactory;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DirectCompiler;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public class ValueCompiler implements DirectCompiler<ValueData> {
    @Override
    public CompiledResolverAlgorithm compile(DataDrivenCompiler compiler, ValueData data, DataDrivenResolverFactory.Context context) {
        var ctx = ((ItemAddon.RecompileContext) context.task().getContext(ItemAddon.instance())).compilationContext();
        var entry = Objects.requireNonNull(ctx.compiledMap.get(data.restrictionEntryReference()));

        var entries = List.of(entry);
        return new ValueCompiled(entry, entries);
    }
}
