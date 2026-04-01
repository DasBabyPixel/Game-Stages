package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenTypes;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.EventRegistry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonKJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.datadriven.NeoItemStackRestrictionEntrySettings;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.RegisterEventJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class ItemKJS implements NeoAddonKJS {
    private final ItemJSParser itemParser = new ItemJSParser();

    @Override
    public void registerEventExtensions(@NonNull EventRegistry registry) {
        var type = registry.get(RegisterEventJS.class);
        type.addFunctionVarArgs("items", (event, cx, args) -> args[0], ItemCollectionWrapper.class, ItemCollectionWrapper.class, ItemCollectionWrapper[].class);
        type.addFunctionVarArgs("restrictItems", this::restrictItems, ItemCollectionWrapper.class, NeoItemRestrictionEntry.class, PreparedRestrictionPredicate.class, ItemCollectionWrapper[].class);
    }

    private NeoItemRestrictionEntry restrictItems(@NonNull RegisterEventJS event, @NonNull KubeJSContext cx, Object @NonNull [] args) {
        return restrictItems(event, cx, (PreparedRestrictionPredicate) args[0], Objects.requireNonNull(((ItemCollectionWrapper) Objects.requireNonNull(args[1])).content()));
    }

    private NeoItemRestrictionEntry restrictItems(@NonNull RegisterEventJS event, @NonNull KubeJSContext cx, @NonNull PreparedRestrictionPredicate predicate, @NonNull GameContent itemsContent) {
        var origin = RestrictionEntryOrigin.string(Objects.requireNonNull(SourceLine.of(cx)).toString());
        var dataDrivenType = DataDrivenTypes
                .instance()
                .get("itemstack_restriction_entry")
                .unsafeCast(ItemStackRestrictionEntry.class);
        var factoryId = "builtin_item";
        var itemStackSettings = new NeoItemStackRestrictionEntrySettings();
        var itemStackRestrictionEntry = new ItemStackRestrictionEntry(predicate, itemStackSettings);

        var networkData = new DataDrivenNetwork.NetworkData<>(dataDrivenType, itemStackRestrictionEntry, factoryId);
        var entry = new NeoItemRestrictionEntry(origin, itemsContent, networkData);
        return event.stageManager().addRestriction(entry);
    }

    @Override
    public void registerTypeWrappers(@NonNull TypeWrapperRegistry registry) {
        registry.register(ItemCollectionWrapper.class, (TypeWrapperRegistry.ContextFromFunction<ItemCollectionWrapper>) (context, o) -> new ItemCollectionWrapper(itemParser.parse(context, o)));
    }
}