package de.dasbabypixel.gamestages.common.addons.item.datadriven.data;

import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ResolverAlgorithm;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ResolverAlgorithmData;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import de.dasbabypixel.gamestages.common.data.PlayerCompilationTask;
import de.dasbabypixel.gamestages.common.data.manager.mutable.AbstractMutableGameStageManager;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public record ValueData(
        ItemStackRestrictionEntryReference restrictionEntryReference) implements DataDrivenData<ValueData.PreCompiled, ValueData.Compiled> {
    public static final String TYPE = "value";

    @Override
    public PreCompiled compile(AbstractMutableGameStageManager<?> manager) {
        var context = manager.get(ItemAddon.MutableStageManagerContext.ATTRIBUTE);
        var entry = context.getEntry(restrictionEntryReference);
        return new PreCompiled(restrictionEntryReference, entry);
    }

    public record PreCompiled(ItemStackRestrictionEntryReference reference,
                              ItemStackRestrictionEntry customData) implements DataDrivenData.PreCompiled<ItemStackRestrictionEntry, Compiled> {
        private static final Algorithm<ItemStackRestrictionEntry> ALGORITHM = new Algorithm<>();

        @Override
        public Compiled compile(PlayerCompilationTask task) {
            var ctx = task.get(ItemAddon.CompilationContext.ATTRIBUTE);
            var entry = Objects.requireNonNull(ctx.compiledMap.get(reference));

            var entries = List.of(entry);
            return new Compiled(entry, entries);
        }

        @Override
        public ResolverAlgorithm<ItemStackRestrictionEntry, ItemStackRestrictionEntry> algorithm() {
            return ALGORITHM;
        }

        @Override
        public List<ItemStackRestrictionEntry> entries() {
            return List.of(customData);
        }
    }

    public record Compiled(CompiledItemStackRestrictionEntry customData,
                           List<CompiledItemStackRestrictionEntry> entries) implements DataDrivenData.Compiled<CompiledItemStackRestrictionEntry> {
        private static final Algorithm<CompiledItemStackRestrictionEntry> ALGORITHM = new Algorithm<>();

        public Compiled {
            entries = List.copyOf(entries);
        }

        @Override
        public ResolverAlgorithm<CompiledItemStackRestrictionEntry, CompiledItemStackRestrictionEntry> algorithm() {
            return ALGORITHM;
        }
    }

    private record Algorithm<CustomData>() implements ResolverAlgorithm<CustomData, CustomData> {
        @Override
        public CustomData resolve(ResolverAlgorithmData<CustomData, CustomData> data, ItemStack itemStack) {
            return data.customData();
        }
    }
}
