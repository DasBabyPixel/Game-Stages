package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential.SequentialCompiler;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential.SequentialData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.value.ValueCompiler;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.value.ValueData;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class DataDrivenCompiler {
    private static final DataDrivenCompiler INSTANCE = new DataDrivenCompiler();
    private final Map<String, Entry<?>> entries = new HashMap<>();

    {
        register(SequentialData.TYPE, SequentialData.class, new SequentialCompiler());
        register(ValueData.TYPE, ValueData.class, new ValueCompiler());
    }

    private DataDrivenCompiler() {
    }

    public <Data extends DataDrivenData> void register(String type, Class<Data> dataCls, DirectCompiler<Data> compiler) {
        entries.put(type, new Entry<>(type, dataCls, compiler));
    }

    public CompiledResolverAlgorithm compile(DataDrivenTypedData<?> data, DataDrivenResolverFactory.Context context) {
        var entry = Objects.requireNonNull(entries.get(data.type()));
        return compile(entry, data.data(), context);
    }

    private <Data extends DataDrivenData> CompiledResolverAlgorithm compile(Entry<Data> entry, DataDrivenData data, DataDrivenResolverFactory.Context context) {
        return Objects.requireNonNull(entry.compiler.compile(this, entry.dataCls.cast(data), context));
    }

    public static DataDrivenCompiler instance() {
        return INSTANCE;
    }

    public record Entry<Data extends DataDrivenData>(String type, Class<Data> dataCls, DirectCompiler<Data> compiler) {
    }
}
