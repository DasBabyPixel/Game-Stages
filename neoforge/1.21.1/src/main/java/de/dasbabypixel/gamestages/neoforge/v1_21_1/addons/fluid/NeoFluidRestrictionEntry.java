package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryPreCompiler;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.CommonFluidRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonFluidCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.types.CommonFluidRestrictionEntry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NeoFluidRestrictionEntry extends CommonFluidRestrictionEntry<NeoFluidRestrictionEntry, NeoFluidRestrictionEntry.PreCompiled> {
    public NeoFluidRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, GameContent targetFluids) {
        super(predicate, origin, targetFluids);
    }

    @Override
    public CustomPacket createPacket(ServerGameStageManager instance) {
        var fluids = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetFluids(), CommonFluidCollection.TYPE);
        return new CommonFluidRestrictionPacket(predicate(), fluids, origin().toString(), hideInJEI());
    }

    @Override
    public PreCompiled precompile(AbstractGameStageManager instance, RestrictionEntryPreCompiler preCompiler) {
        var fluids = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetFluids(), CommonFluidCollection.TYPE);
        return new PreCompiled(fluids);
    }

    @Override
    public CompiledRestrictionEntry compile(RecompilationTask task, PreCompiled preCompiled) {
        return new Compiled(this, preCompiled.fluids, task.predicateCompiler().compile(predicate()));
    }

    public record Compiled(NeoFluidRestrictionEntry entry, CommonFluidCollection gameContent,
                           CompiledRestrictionPredicate predicate) implements CompiledRestrictionEntry {
        @Override
        public RestrictionEntryOrigin origin() {
            return entry.origin();
        }
    }

    public record PreCompiled(CommonFluidCollection fluids) {
    }
}
