package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.predicate;

import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledResolverAlgorithm;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import net.minecraft.advancements.critereon.ItemPredicate;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public record PredicateData(ItemPredicate predicate,
                            ItemStackRestrictionEntryReference resultReference) implements DataDrivenData<PredicateData.PreCompiled, PredicateData.Compiled> {
    public static final String TYPE = "predicate";

    @Override
    public PreCompiled precompile(AbstractGameStageManager<?> abstractGameStageManager) {
        return new PreCompiled(predicate, resultReference);
    }

    public record PreCompiled(ItemPredicate predicate,
                              ItemStackRestrictionEntryReference resultReference) implements DataDrivenData.PreCompiled<Compiled> {
        @Override
        public Compiled compile(RecompilationTask task) {
            var ctx = task.get(ItemAddon.CompilationContext.ATTRIBUTE);
            var entry = Objects.requireNonNull(ctx.compiledMap.get(resultReference), "Missing compiled predicate for key " + resultReference.referenceId());
            return new Compiled(entry, predicate);
        }
    }

    public record Compiled(CompiledItemStackRestrictionEntry entry, List<CompiledItemStackRestrictionEntry> entries,
                           ItemPredicate predicate) implements CompiledResolverAlgorithm {
        public Compiled(CompiledItemStackRestrictionEntry entry, ItemPredicate predicate) {
            this(entry, List.of(entry), predicate);
        }

        @SuppressWarnings("DataFlowIssue")
        @Override
        public @Nullable CompiledItemStackRestrictionEntry resolve(ItemStack itemStack) {
            if (predicate.test((net.minecraft.world.item.ItemStack) (Object) itemStack)) {
                return entry;
            }
            return null;
        }
    }
}
