package de.dasbabypixel.gamestages.common.addons.item.datadriven.value;

import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledResolverAlgorithm;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public record ValueData(
        ItemStackRestrictionEntryReference restrictionEntryReference) implements DataDrivenData<ValueData.PreCompiled, ValueData.Compiled> {
    public static final String TYPE = "value";

    @Override
    public PreCompiled precompile(AbstractGameStageManager<?> abstractGameStageManager) {
        return new PreCompiled(restrictionEntryReference);
    }

    public record PreCompiled(
            ItemStackRestrictionEntryReference reference) implements DataDrivenData.PreCompiled<Compiled> {
        @Override
        public Compiled compile(RecompilationTask task) {
            var ctx = task.get(ItemAddon.CompilationContext.ATTRIBUTE);
            var entry = Objects.requireNonNull(ctx.compiledMap.get(reference));

            var entries = List.of(entry);
            return new Compiled(entry, entries);
        }
    }

    public record Compiled(CompiledItemStackRestrictionEntry entry,
                           List<CompiledItemStackRestrictionEntry> entries) implements CompiledResolverAlgorithm {
        public Compiled {
            entries = List.copyOf(entries);
        }

        @Override
        public CompiledItemStackRestrictionEntry resolve(ItemStack itemStack) {
            return entry;
        }
    }
}
