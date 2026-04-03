package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import org.jspecify.annotations.NonNull;

public interface DirectCompiler<Data extends DataDrivenData> {
    @NonNull CompiledResolverAlgorithm compile(@NonNull DataDrivenCompiler compiler, Data data, DataDrivenResolverFactory.@NonNull Context context);
}
