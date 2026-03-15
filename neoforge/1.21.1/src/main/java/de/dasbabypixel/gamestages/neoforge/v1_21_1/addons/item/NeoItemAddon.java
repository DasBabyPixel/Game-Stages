package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.VItemAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.*;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.RegisterEventJS;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.rhino.type.TypeInfo;

import static de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.KJSHelper.drop;

public class NeoItemAddon extends VItemAddon implements NeoAddon {
    @Override
    public void handle(CommonItemRestrictionPacket packet) {
        var entry = new NeoItemRestrictionEntry(packet.predicate(), RestrictionEntryOrigin.string(packet.origin()), packet.targetCollection());
        entry.setHideInJEI(packet.hideInJEI());
        entry.setHideTooltip(packet.hideTooltip());
        entry.setRenderItemName(packet.renderItemName());
        ClientGameStageManager.instance().addRestriction(entry);
    }

    @Override
    public NeoAddonKJS createKubeJSSupport() {
        return new KJS();
    }

    @Override
    public NeoAddonJEI createJEISupport() {
        return new ItemJEI();
    }

    @Override
    public NeoAddonProbeJS createProbeJSSupport() {
        return new ItemProbeJS();
    }

    public static class KJS implements NeoAddonKJS {
        private final ItemJSParser itemParser = new ItemJSParser();

        @Override
        public void registerEventExtensions(EventRegistry registry) {
            var predicateType = TypeInfo.of(PreparedRestrictionPredicate.class);
            var type = registry.get(RegisterEventJS.class);
            type.addFunction("items", (event, cx, args) -> itemParser.parse(cx, args));
            type.addFunction("restrictItems", (event, cx, args) -> {
                var itemsContent = itemParser.parse(cx, drop(args, 1));
                var predicate = (PreparedRestrictionPredicate) cx.jsToJava(args[0], predicateType);
                var source = SourceLine.of(cx).toString();
                return event
                        .stageManager()
                        .addRestriction(new NeoItemRestrictionEntry(predicate, RestrictionEntryOrigin.string(source), itemsContent));
            });
        }

        @Override
        public void registerTypeWrappers(TypeWrapperRegistry registry) {
            registry.register(ItemCollectionWrapper.class, (TypeWrapperRegistry.ContextFromFunction<ItemCollectionWrapper>) (context, o) -> new ItemCollectionWrapper(itemParser.parse(context, o)));
        }
    }
}
