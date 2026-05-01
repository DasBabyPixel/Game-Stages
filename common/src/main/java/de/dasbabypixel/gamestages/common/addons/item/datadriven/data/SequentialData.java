package de.dasbabypixel.gamestages.common.addons.item.datadriven.data;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ResolverAlgorithm;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ResolverAlgorithmData;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import de.dasbabypixel.gamestages.common.data.PlayerCompilationTask;
import de.dasbabypixel.gamestages.common.data.manager.mutable.AbstractMutableGameStageManager;
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
    public PreCompiled compile(AbstractMutableGameStageManager<?> manager) {
        var subAlgorithms = new ArrayList<DataDrivenData.PreCompiled<?, ?>>();
        var entries = new ArrayList<ItemStackRestrictionEntry>();
        for (var value : values) {
            var subAlgorithm = value.data().compile(manager);
            subAlgorithms.add(subAlgorithm);
            entries.addAll(subAlgorithm.entries());
        }
        return new PreCompiled(subAlgorithms, entries);
    }

    public record PreCompiled(List<DataDrivenData.PreCompiled<?, ?>> subAlgorithms,
                              List<ItemStackRestrictionEntry> entries) implements DataDrivenData.PreCompiled<List<DataDrivenData.PreCompiled<?, ?>>, Compiled> {
        private static final Algorithm<DataDrivenData.PreCompiled<?, ?>, ItemStackRestrictionEntry> ALGORITHM = new Algorithm<>();

        public PreCompiled {
            subAlgorithms = List.copyOf(subAlgorithms);
            entries = List.copyOf(entries);
        }

        @Override
        public Compiled compile(PlayerCompilationTask task) {
            var subAlgorithms = new ArrayList<DataDrivenData.Compiled<?>>();
            var entries = new ArrayList<CompiledItemStackRestrictionEntry>();
            for (var entry : this.subAlgorithms) {
                var compiled = entry.compile(task);
                subAlgorithms.add(compiled);
                entries.addAll(compiled.entries());
            }
            return new Compiled(subAlgorithms, entries);
        }

        @Override
        public ResolverAlgorithm<List<DataDrivenData.PreCompiled<?, ?>>, ItemStackRestrictionEntry> algorithm() {
            return ALGORITHM;
        }

        @Override
        public List<DataDrivenData.PreCompiled<?, ?>> customData() {
            return subAlgorithms;
        }
    }

    public record Compiled(List<DataDrivenData.Compiled<?>> customData,
                           List<CompiledItemStackRestrictionEntry> entries) implements DataDrivenData.Compiled<List<DataDrivenData.Compiled<?>>> {
        private static final Algorithm<DataDrivenData.Compiled<?>, CompiledItemStackRestrictionEntry> ALGORITHM = new Algorithm<>();

        public Compiled {
            customData = List.copyOf(customData);
            entries = List.copyOf(entries);
        }

        @Override
        public ResolverAlgorithm<List<DataDrivenData.Compiled<?>>, CompiledItemStackRestrictionEntry> algorithm() {
            return ALGORITHM;
        }
    }

    private record Algorithm<Entry extends ResolverAlgorithmData<?, Fin>, Fin>() implements ResolverAlgorithm<List<Entry>, Fin> {
        @Override
        public @Nullable Fin resolve(ResolverAlgorithmData<List<Entry>, Fin> data, ItemStack itemStack) {
            for (var subAlgorithm : data.customData()) {
                var entry = subAlgorithm.resolve(itemStack);
                if (entry != null) return entry;
            }
            return null;
        }
    }
}
