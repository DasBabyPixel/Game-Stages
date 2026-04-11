package de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledResolverAlgorithm;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public record SequentialData(
        List<DataDrivenTypedData<?>> values) implements DataDrivenData<SequentialData.PreCompiled, SequentialData.Compiled> {
    public static final String TYPE = "sequential";

    public SequentialData {
        values = List.copyOf(values);
    }

    @Override
    public PreCompiled precompile(AbstractGameStageManager<?> manager) {
        var entries = new ArrayList<PreCompiled.Entry<?>>();
        for (var value : values) {
            var entry = preCompile(manager, value.type(), value.data());
            entries.add(entry);
        }
        return new PreCompiled(entries);
    }

    @SuppressWarnings({"unchecked", "CastCanBeRemovedNarrowingVariableType"})
    private static <PC extends DataDrivenData.PreCompiled<?>> SequentialData.PreCompiled.Entry<PC> preCompile(AbstractGameStageManager<?> manager, String type, DataDrivenData<PC, ?> data) {
        // For some reason we need to cast this, using PC directly breaks the compiler?
        DataDrivenData.PreCompiled<?> a = data.precompile(manager);
        return new SequentialData.PreCompiled.Entry<>(data, (PC) a);
    }

    public record PreCompiled(List<Entry<?>> entries) implements DataDrivenData.PreCompiled<Compiled> {
        public PreCompiled {
            entries = List.copyOf(entries);
        }

        @Override
        public Compiled compile(RecompilationTask task) {
            var subAlgorithms = new ArrayList<CompiledResolverAlgorithm>();
            var entries = new ArrayList<CompiledItemStackRestrictionEntry>();
            for (var entry : this.entries) {
                var compiled = entry.compile(task);
                subAlgorithms.add(compiled);
                entries.addAll(compiled.entries());
            }
            return new Compiled(subAlgorithms, entries);
        }

        public record Entry<PreCompiled extends DataDrivenData.PreCompiled<?>>(DataDrivenData<PreCompiled, ?> data,
                                                                               PreCompiled preCompiled) {
            private CompiledResolverAlgorithm compile(RecompilationTask task) {
                return preCompiled.compile(task);
            }
        }
    }

    public record Compiled(List<CompiledResolverAlgorithm> subAlgorithms,
                           List<CompiledItemStackRestrictionEntry> entries) implements CompiledResolverAlgorithm {
        public Compiled {
            subAlgorithms = List.copyOf(subAlgorithms);
            entries = List.copyOf(entries);
        }

        @Override
        public @Nullable CompiledItemStackRestrictionEntry resolve(ItemStack itemStack) {
            for (var subAlgorithm : subAlgorithms) {
                var entry = subAlgorithm.resolve(itemStack);
                if (entry != null) return entry;
            }
            return null;
        }
    }
}
