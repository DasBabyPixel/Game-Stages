package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.EventRegistry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonKJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.RegisterEventJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;

public class ItemKJS implements NeoAddonKJS {
    private final ItemJSParser itemParser = new ItemJSParser();

    @Override
    public void registerEventExtensions(EventRegistry registry) {
        var type = registry.get(RegisterEventJS.class);
        type.addFunctionVarArgs("items", (event, cx, args) -> args[0], ItemCollectionWrapper.class, ItemCollectionWrapper.class, ItemCollectionWrapper[].class);
        type.addFunctionVarArgs("restrictItems", (event, cx, args) -> restrictItems(event, cx, (PreparedRestrictionPredicate) args[0], ((ItemCollectionWrapper) args[1]).content()), ItemCollectionWrapper.class, NeoItemRestrictionEntry.class, PreparedRestrictionPredicate.class, ItemCollectionWrapper[].class);
    }

    private NeoItemRestrictionEntry restrictItems(RegisterEventJS event, KubeJSContext cx, PreparedRestrictionPredicate predicate, GameContent itemsContent) {
        return event
                .stageManager()
                .addRestriction(new NeoItemRestrictionEntry(predicate, RestrictionEntryOrigin.string(SourceLine
                        .of(cx)
                        .toString()), itemsContent));
    }

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry registry) {
        registry.register(ItemCollectionWrapper.class, (TypeWrapperRegistry.ContextFromFunction<ItemCollectionWrapper>) (context, o) -> new ItemCollectionWrapper(itemParser.parse(context, o)));
    }
}