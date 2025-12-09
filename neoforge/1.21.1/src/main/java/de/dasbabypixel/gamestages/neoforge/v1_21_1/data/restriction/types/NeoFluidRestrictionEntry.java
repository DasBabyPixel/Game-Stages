package de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.types.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonFluidCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.types.CommonFluidRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.CommonFluidRestrictionPacket;
import org.jspecify.annotations.NonNull;

public class NeoFluidRestrictionEntry extends CommonFluidRestrictionEntry<NeoFluidRestrictionEntry, NeoFluidRestrictionEntry.PreCompiled> {
    public NeoFluidRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate, @NonNull RestrictionEntryOrigin origin, @NonNull GameContent targetFluids) {
        super(predicate, origin, targetFluids);
    }

    @Override
    public @NonNull CustomPacket createPacket(@NonNull ServerGameStageManager instance) {
        var fluids = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetFluids(), CommonFluidCollection.TYPE);
        return new CommonFluidRestrictionPacket(predicate(), fluids, origin().toString(), hideInJEI());
    }

    @Override
    public @NonNull PreCompiled precompile(@NonNull AbstractGameStageManager instance) {
        var fluids = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetFluids(), CommonFluidCollection.TYPE);
        return new PreCompiled(fluids);
    }

    @Override
    public @NonNull CompiledRestrictionEntry compile(@NonNull AbstractGameStageManager instance, @NonNull PreCompiled preCompiled, @NonNull CompiledRestrictionPredicate predicate) {
        return new Compiled(this, preCompiled.fluids, predicate);
    }

    public record Compiled(@NonNull NeoFluidRestrictionEntry entry, @NonNull CommonFluidCollection gameContent,
                           @NonNull CompiledRestrictionPredicate predicate) implements CompiledRestrictionEntry {
        @Override
        public @NonNull RestrictionEntryOrigin origin() {
            return entry.origin();
        }
    }

    public record PreCompiled(@NonNull CommonFluidCollection fluids) {
    }
}
