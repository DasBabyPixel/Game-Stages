package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential.SequentialCompiler;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential.SequentialData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.value.ValueCompiler;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.value.ValueData;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataDrivenCompiler {
    private static final @NonNull DataDrivenCompiler INSTANCE = new DataDrivenCompiler();
    private final @NonNull Map<String, Entry<?>> entries = new HashMap<>();

    {
        register("sequential", SequentialData.class, new SequentialCompiler());
        register("value", ValueData.class, new ValueCompiler());
    }

    private DataDrivenCompiler() {
    }

    public <Data extends DataDrivenData> void register(@NonNull String type, @NonNull Class<Data> dataCls, @NonNull DirectCompiler<Data> compiler) {
        entries.put(type, new Entry<>(type, dataCls, compiler));
    }

    public @NonNull CompiledResolverAlgorithm compile(@NonNull DataDrivenTypedData<?> data, DataDrivenResolverFactory.@NonNull Context context) {
        var entry = Objects.requireNonNull(entries.get(data.type()));
        return compile(entry, data.data(), context);
    }

    private <Data extends DataDrivenData> @NonNull CompiledResolverAlgorithm compile(@NonNull Entry<Data> entry, DataDrivenData data, DataDrivenResolverFactory.@NonNull Context context) {
        return Objects.requireNonNull(entry.compiler.compile(this, entry.dataCls.cast(data), context));
    }

    public static @NonNull DataDrivenCompiler instance() {
        return INSTANCE;
    }

    public record Entry<Data extends DataDrivenData>(@NonNull String type, @NonNull Class<Data> dataCls,
                                                     @NonNull DirectCompiler<Data> compiler) {
    }
}
