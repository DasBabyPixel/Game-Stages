package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.VFluidAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.CommonFluidRestrictionPacket;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.*;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.RegisterEventJS;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.rhino.type.TypeInfo;

import static de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.KJSHelper.drop;

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
            var predicateType = TypeInfo.of(PreparedRestrictionPredicate.class);
            var type = registry.get(RegisterEventJS.class);
            type.addFunction("fluids", (event, cx, args) -> fluidParser.parse(cx, args));
            type.addFunction("restrictFluids", (event, cx, args) -> {
                var fluidsContent = fluidParser.parse(cx, drop(args, 1));
                var predicate = (PreparedRestrictionPredicate) cx.jsToJava(args[0], predicateType);
                var source = SourceLine.of(cx).toString();
                return event
                        .stageManager()
                        .addRestriction(new NeoFluidRestrictionEntry(predicate, RestrictionEntryOrigin.string(source), fluidsContent));
            });
        }

        @Override
        public void registerTypeWrappers(TypeWrapperRegistry registry) {
            registry.register(FluidCollectionWrapper.class, (TypeWrapperRegistry.ContextFromFunction<FluidCollectionWrapper>) (context, o) -> new FluidCollectionWrapper(fluidParser.parse(context, o)));
        }
    }
}
