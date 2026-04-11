package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.CommonFluidRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.VFluidAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.*;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.RegisterEventJS;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class NeoFluidAddon extends VFluidAddon implements NeoAddon {
    @Override
    public void handle(CommonFluidRestrictionPacket packet) {
        var entry = new NeoFluidRestrictionEntry(packet.predicate(), RestrictionEntryOrigin.string(packet.origin()), packet.targetCollection());
        entry.setHideInJEI(packet.hideInJEI());
        ClientGameStageManager.instance().addRestriction(entry);
    }

    @Override
    public NeoAddonKJS createKubeJSSupport() {
        return new KJS();
    }

    @Override
    public NeoAddonJEI createJEISupport() {
        return new FluidJEI();
    }

    @Override
    public NeoAddonProbeJS createProbeJSSupport() {
        return new FluidProbeJS();
    }

    public static class KJS implements NeoAddonKJS {
        private final FluidJSParser fluidParser = new FluidJSParser();

        @Override
        public void registerEventExtensions(EventRegistry registry) {
            var type = registry.get(RegisterEventJS.class);
            type.addFunctionVarArgs("fluids", (event, cx, args) -> args[0], FluidCollectionWrapper.class, FluidCollectionWrapper.class, FluidCollectionWrapper[].class);
            type.addFunctionVarArgs("restrictFluids", (event, cx, args) -> {
                var fluidsContent = ((FluidCollectionWrapper) Objects.requireNonNull(args[1])).content();
                var predicate = (PreparedRestrictionPredicate) Objects.requireNonNull(args[0]);
                var source = SourceLine.of(cx).toString();
                return event
                        .stageManager()
                        .addRestriction(new NeoFluidRestrictionEntry(predicate, RestrictionEntryOrigin.string(source), fluidsContent));
            }, FluidCollectionWrapper.class, NeoFluidRestrictionEntry.class, PreparedRestrictionPredicate.class, FluidCollectionWrapper[].class);
        }

        @Override
        public void registerTypeWrappers(TypeWrapperRegistry registry) {
            registry.register(FluidCollectionWrapper.class, (TypeWrapperRegistry.ContextFromFunction<FluidCollectionWrapper>) (context, o) -> new FluidCollectionWrapper(fluidParser.parse(context, o)));
        }
    }
}
