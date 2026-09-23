package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.data.PlayerCompilationTask;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.compiler.ManagerCompilerTask;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.CommonFluidRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.FluidContentWrapper;
import de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.types.CommonFluidRestrictionEntry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NeoFluidRestrictionEntry extends CommonFluidRestrictionEntry<NeoFluidRestrictionEntry, NeoFluidRestrictionEntry.PreCompiled, NeoFluidRestrictionEntry.Compiled> {
    public NeoFluidRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, FluidContentWrapper gameContent) {
        super(predicate, origin, gameContent);
    }

    @Override
    public PreCompiled compile(ManagerCompilerTask task) {
        return new PreCompiled(predicate(), gameContent(), hideInJEI(), origin());
    }

    public record Compiled(PreCompiled preCompiled, FluidContentWrapper gameContent,
                           CompiledRestrictionPredicate predicate,
                           boolean hideInJEI) implements CompiledRestrictionEntry<Compiled, PreCompiled> {
    }

    public record PreCompiled(PreparedRestrictionPredicate predicate, FluidContentWrapper gameContent,
                              boolean hideInJEI,
                              RestrictionEntryOrigin origin) implements RestrictionEntry.PreCompiled<PreCompiled, Compiled> {
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
