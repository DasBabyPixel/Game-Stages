package de.dasbabypixel.gamestages.common.addons.item.datadriven.data;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.*;
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
        var subAlgorithms = new ArrayList<DataDrivenData.PreCompiled<?, ?>>();
        for (var value : values) {
            var subAlgorithm = value.data().precompile(manager);
            subAlgorithms.add(subAlgorithm);
        }
        return new PreCompiled(subAlgorithms);
    }

    public record PreCompiled(
            List<DataDrivenData.PreCompiled<?, ?>> subAlgorithms) implements DataDrivenData.PreCompiled<List<DataDrivenData.PreCompiled<?, ?>>, Compiled> {
        private static final Algorithm<DataDrivenData.PreCompiled<?, ?>, ItemStackRestrictionEntry> ALGORITHM = new Algorithm<>();

        public PreCompiled {
            subAlgorithms = List.copyOf(subAlgorithms);
        }

        @Override
        public Compiled compile(RecompilationTask task) {
            var subAlgorithms = new ArrayList<DataDrivenData.Compiled<?>>();
            for (var entry : this.subAlgorithms) {
                var compiled = entry.compile(task);
                subAlgorithms.add(compiled);
            }
            return new Compiled(subAlgorithms);
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

    public record Compiled(
            List<DataDrivenData.Compiled<?>> customData) implements DataDrivenData.Compiled<List<DataDrivenData.Compiled<?>>> {
        private static final Algorithm<DataDrivenData.Compiled<?>, CompiledItemStackRestrictionEntry> ALGORITHM = new Algorithm<>();

        public Compiled {
            customData = List.copyOf(customData);
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
