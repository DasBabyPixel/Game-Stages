package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.data.PlayerCompilationTask;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.AbstractMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.CommonFluidRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonFluidCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.types.CommonFluidRestrictionEntry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NeoFluidRestrictionEntry extends CommonFluidRestrictionEntry<NeoFluidRestrictionEntry, NeoFluidRestrictionEntry.PreCompiled, NeoFluidRestrictionEntry.Compiled> {
    public NeoFluidRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, TypedGameContent targetFluids) {
        super(predicate, origin, targetFluids);
    }

    @Override
    public PreCompiled compile(AbstractMutableGameStageManager<?> instance) {
        var fluids = instance.get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetFluids(), CommonFluidCollection.TYPE);
        return new PreCompiled(this, predicate(), fluids, hideInJEI());
    }

    public record Compiled(PreCompiled preCompiled, CommonFluidCollection gameContent,
                           CompiledRestrictionPredicate predicate,
                           boolean hideInJEI) implements CompiledRestrictionEntry<Compiled, PreCompiled> {
    }

    public record PreCompiled(NeoFluidRestrictionEntry entry, PreparedRestrictionPredicate predicate,
                              CommonFluidCollection gameContent,
                              boolean hideInJEI) implements RestrictionEntry.PreCompiled<PreCompiled, Compiled> {
        @Override
        public Compiled compile(PlayerCompilationTask task) {
            return new Compiled(this, gameContent, task.predicateCompiler().compile(predicate), hideInJEI);
        }

        @Override
        public CustomPacket createPacket(ServerGameStageManager instance) {
            return new CommonFluidRestrictionPacket(predicate(), gameContent(), origin().toString(), hideInJEI());
        }
    }
}
