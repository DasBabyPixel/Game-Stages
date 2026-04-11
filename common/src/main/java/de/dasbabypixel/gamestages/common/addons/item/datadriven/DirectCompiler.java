package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface DirectCompiler<Data extends DataDrivenData> {
    CompiledResolverAlgorithm compile(DataDrivenCompiler compiler, Data data, DataDrivenResolverFactory.Context context);
}
