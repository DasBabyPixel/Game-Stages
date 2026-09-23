package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.data.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.GameContentWrapper;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ClientMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.CommonFluidRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.FluidContentWrapper;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.FluidType;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.VFluidAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.EventRegistry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonKJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei.JEIIntegration;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.StagesKubeJSPlugin;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.ServerRegisterEventJS;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class NeoFluidAddon extends VFluidAddon implements NeoAddon {
    public NeoFluidAddon() {
        JEIIntegration.INIT_JEI_SUPPORT_EVENT.addListener(this::initJEISupport);
    }

    @Override
    public void handle(CommonFluidRestrictionPacket packet) {
        var entry = new NeoFluidRestrictionEntry(packet.predicate(), RestrictionEntryOrigin.string(packet.origin()), packet.targetCollection());
        entry.setHideInJEI(packet.hideInJEI());
        ClientMutableGameStageManager.buildingInstance().addRestriction(entry);
    }

    @Override
    public NeoAddonKJS createKubeJSSupport() {
        return new KJS();
    }

    private void initJEISupport(JEIIntegration.InitJEISupportEvent event) {
        FluidJEI.init();
    }

    @Override
    public NeoAddonProbeJS createProbeJSSupport() {
        return new FluidProbeJS();
    }

    public static class KJS implements NeoAddonKJS {
        private final FluidJSParser fluidParser = new FluidJSParser();

        {
            StagesKubeJSPlugin.register(FluidType.get(), fluidParser::parse);
        }

        @Override
        public void registerEventExtensions(EventRegistry registry) {
            var type = registry.get(ServerRegisterEventJS.class);
            var fluidType = StagesKubeJSPlugin.typedCollection(FluidType.get());
            var fluidTypeArray = fluidParser.param(fluidType.asArray());
            type.addFunctionVarArgs("fluids", fluidParser::parse, fluidType, fluidTypeArray);
            type.addFunctionVarArgs("restrictFluids", (call, cx, args) -> {
                var event = call.event();
                var flattener = event.stageManager().get(GameContentFlattener.MUTABLE_MANAGER_ATTRIBUTE);
                var gameContent = flattener.flatten(((GameContentWrapper) args[1]).gameContent(), FluidType.get());
                var predicate = (PreparedRestrictionPredicate) Objects.requireNonNull(args[0]);
                return event
                        .stageManager()
                        .addRestriction(new NeoFluidRestrictionEntry(predicate, cx.origin(), new FluidContentWrapper(gameContent)));
            }, NeoFluidRestrictionEntry.class, PreparedRestrictionPredicate.class, fluidTypeArray);
        }
    }
}
