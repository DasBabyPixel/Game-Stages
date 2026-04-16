package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.data;

import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ResolverAlgorithm;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ResolverAlgorithmData;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import it.unimi.dsi.fastutil.Pair;
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
    public PreCompiled precompile(AbstractGameStageManager<?> manager) {
        var entry = manager.get(ItemAddon.STAGE_MANAGER_CONTEXT).getEntry(resultReference);
        return new PreCompiled(Pair.of(entry, predicate), resultReference);
    }

    public record PreCompiled(Pair<ItemStackRestrictionEntry, ItemPredicate> customData,
                              ItemStackRestrictionEntryReference resultReference) implements DataDrivenData.PreCompiled<Pair<ItemStackRestrictionEntry, ItemPredicate>, Compiled> {
        private static final Algorithm<ItemStackRestrictionEntry> ALGORITHM = new Algorithm<>();

        @Override
        public Compiled compile(RecompilationTask task) {
            var ctx = task.get(ItemAddon.CompilationContext.ATTRIBUTE);
            var entry = Objects.requireNonNull(ctx.compiledMap.get(resultReference), "Missing compiled predicate for key " + resultReference.referenceId());
            return new Compiled(Pair.of(entry, customData.second()));
        }

        @Override
        public List<ItemStackRestrictionEntry> entries() {
            return List.of(customData.first());
        }

        @Override
        public ResolverAlgorithm<Pair<ItemStackRestrictionEntry, ItemPredicate>, ItemStackRestrictionEntry> algorithm() {
            return ALGORITHM;
        }
    }

    public record Compiled(
            Pair<CompiledItemStackRestrictionEntry, ItemPredicate> customData) implements DataDrivenData.Compiled<Pair<CompiledItemStackRestrictionEntry, ItemPredicate>> {
        private static final Algorithm<CompiledItemStackRestrictionEntry> ALGORITHM = new Algorithm<>();

        @Override
        public ResolverAlgorithm<Pair<CompiledItemStackRestrictionEntry, ItemPredicate>, CompiledItemStackRestrictionEntry> algorithm() {
            return ALGORITHM;
        }

        @Override
        public List<CompiledItemStackRestrictionEntry> entries() {
            return List.of(customData.first());
        }
    }

    private record Algorithm<Fin>() implements ResolverAlgorithm<Pair<Fin, ItemPredicate>, Fin> {
        @SuppressWarnings("DataFlowIssue")
        @Override
        public @Nullable Fin resolve(ResolverAlgorithmData<Pair<Fin, ItemPredicate>, Fin> data, ItemStack itemStack) {
            if (data.customData().second().test((net.minecraft.world.item.ItemStack) (Object) itemStack)) {
                return data.customData().first();
            }
            return null;
        }
    }
}
