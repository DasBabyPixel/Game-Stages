package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
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
    public CustomPacket createPacket(ServerGameStageManager instance) {
        var fluids = instance.get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetFluids(), CommonFluidCollection.TYPE);
        return new CommonFluidRestrictionPacket(predicate(), fluids, origin().toString(), hideInJEI());
    }

    @Override
    public PreCompiled precompile(AbstractGameStageManager<?> instance) {
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
        public Compiled compile(RecompilationTask task) {
            return new Compiled(this, gameContent, task.predicateCompiler().compile(predicate), hideInJEI);
        }
    }
}
