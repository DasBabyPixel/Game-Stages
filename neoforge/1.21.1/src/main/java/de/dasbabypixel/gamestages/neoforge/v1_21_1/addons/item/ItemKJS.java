package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.data.SequentialData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.data.ValueData;
import de.dasbabypixel.gamestages.common.data.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.GameContentWrapper;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.util.ParameterizedTypeImpl;
import de.dasbabypixel.gamestages.common.util.WildcardTypeImpl;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.ItemContentWrapper;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.ItemType;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.data.PredicateData;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.settings.VItemStackRestrictionEntrySettings;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenTypes;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.EventRegistry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonKJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jsapi.ItemRestrictionSettingsJSImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jsapi.ItemStackRestrictionEntryJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jsapi.ItemStackRestrictionEntryJSImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jsapi.ItemStacksRestrictionEntryJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jsapi.ItemStacksRestrictionEntryJSImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jsapi.ItemsRestrictionEntryJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jsapi.ItemsRestrictionEntryJSImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSContext;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.StagesKubeJSPlugin;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.ServerRegisterEventJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.startup.StartupRegisterEventJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.RegistryOps;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class ItemKJS implements NeoAddonKJS {
    private static final TypeInfo JSON_TYPE = Objects.requireNonNull(TypeInfo.of(JsonElement.class));
    private final ItemJSParser itemParser = new ItemJSParser();

    {
        StagesKubeJSPlugin.register(ItemType.get(), itemParser::parse);
    }

    @Override
    public void registerEventExtensions(EventRegistry registry) {
        startupRegisterExtensions(registry.get(StartupRegisterEventJS.class));
        serverRegisterExtensions(registry.get(ServerRegisterEventJS.class));
    }

    private void startupRegisterExtensions(EventType<StartupRegisterEventJS> type) {
        type.addFunction("itemDefaultSettings", (event, cx, args) -> VItemStackRestrictionEntrySettings.DEFAULT_SETTINGS, VItemStackRestrictionEntrySettings.class);
    }

    private void serverRegisterExtensions(EventType<ServerRegisterEventJS> type) {
        var typeEntry = ItemType.get();
        var itemTypeArray = itemParser.param(typeEntry);
        var dataDrivenTypedDataType = new ParameterizedTypeImpl(DataDrivenTypedData.class, WildcardTypeImpl.NO_BOUNDS);
        type.addFunctionVarArgs("items", itemParser::parse, ItemType.get(), itemTypeArray);
        type.addFunctionVarArgs("restrictItems", this::restrictItems, ItemsRestrictionEntryJS.class, PreparedRestrictionPredicate.class, itemTypeArray);
        type.addFunction("registerItemStackEntry", this::registerItemStackEntry, ItemStackRestrictionEntryJS.class, PreparedRestrictionPredicate.class);
        type.addFunctionVarArgs("restrictItemStacks", this::restrictItemStacks, ItemStacksRestrictionEntryJS.class, dataDrivenTypedDataType, itemTypeArray);
        type.addFunction("restrictedItems", (call, cx, args) -> call
                .event()
                .stageManager()
                .restrictedContent(ItemType.get()), ItemType.get());
    }

    private ItemStackRestrictionEntryJS registerItemStackEntry(EventType.FunctionCall<? extends ServerRegisterEventJS> call, JSContext cx, Object[] args) {
        var event = call.event();
        var predicate = (PreparedRestrictionPredicate) Objects.requireNonNull(args[0]);
        var compilationContext = event
                .stageManager()
                .get(ItemAddon.MutableStageManagerContext.MUTABLE_MANAGER_ATTRIBUTE);
        var settings = VItemStackRestrictionEntrySettings.create(event.stageManager());
        var restrictionEntry = new ItemStackRestrictionEntry(predicate, settings);
        var reference = compilationContext.addRestrictionEntry(restrictionEntry);

        return new ItemStackRestrictionEntryJSImpl(new ItemRestrictionSettingsJSImpl(settings), predicate, reference);
    }

    private ItemStacksRestrictionEntryJS restrictItemStacks(EventType.FunctionCall<? extends ServerRegisterEventJS> call, JSContext cx, Object[] args) {
        var event = call.event();
        var origin = cx.origin();
        var data = (DataDrivenTypedData<?>) Objects.requireNonNull(args[0]);
        var flattener = event.stageManager().get(GameContentFlattener.MUTABLE_MANAGER_ATTRIBUTE);
        var items = new ItemContentWrapper(flattener.flatten(((GameContentWrapper) Objects.requireNonNull(args[1])).gameContent(), ItemType.get()));
        var dataDrivenType = DataDrivenTypes.instance().get(data.type()).unsafeCast();
        var factoryId = "data_driven";

        var networkData = new DataDrivenNetwork.NetworkData<>(dataDrivenType, Objects.requireNonNull(data.data()), factoryId);
        var entry = new CommonItemRestrictionEntry(origin, items, networkData);
        event.stageManager().addRestriction(entry);
        return new ItemStacksRestrictionEntryJSImpl(items);
    }

    private ItemsRestrictionEntryJS restrictItems(EventType.FunctionCall<? extends ServerRegisterEventJS> call, JSContext cx, Object[] args) {
        var event = call.event();
        var flattener = event.stageManager().get(GameContentFlattener.MUTABLE_MANAGER_ATTRIBUTE);
        return restrictItems(event, cx, (PreparedRestrictionPredicate) args[0], new ItemContentWrapper(flattener.flatten(((GameContentWrapper) Objects.requireNonNull(args[1])).gameContent(), ItemType.get())));
    }

    private ItemsRestrictionEntryJS restrictItems(ServerRegisterEventJS event, JSContext cx, PreparedRestrictionPredicate predicate, ItemContentWrapper itemsContent) {
        var origin = cx.origin();
        var dataDrivenType = DataDrivenTypes.instance().get(ValueData.TYPE).unsafeCast();
        var factoryId = "data_driven";
        var itemStackSettings = VItemStackRestrictionEntrySettings.create(event.stageManager());
        var itemStackRestrictionEntry = new ItemStackRestrictionEntry(predicate, itemStackSettings);
        var reference = event
                .stageManager()
                .get(ItemAddon.MutableStageManagerContext.MUTABLE_MANAGER_ATTRIBUTE)
                .addRestrictionEntry(itemStackRestrictionEntry);
        var data = new ValueData(reference);

        var networkData = new DataDrivenNetwork.NetworkData<>(dataDrivenType, data, factoryId);
        var entry = new CommonItemRestrictionEntry(origin, itemsContent, networkData);
        event.stageManager().addRestriction(entry);

        return new ItemsRestrictionEntryJSImpl(new ItemRestrictionSettingsJSImpl(itemStackSettings), predicate, itemsContent);
    }

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry registry) {
        registry.register(DataDrivenTypedData.class, (context, o, typeInfo) -> parse((KubeJSContext) Objects.requireNonNull(context), o));
        registry.register(ItemStackRestrictionEntryReference.class, (context, o, typeInfo) -> switch (o) {
            case ItemStackRestrictionEntryReference ref -> ref;
            case ItemStackRestrictionEntryJSImpl r -> r.reference();
            case null, default -> throw new IllegalStateException("Unexpected value: " + o);
        });
    }

    private DataDrivenTypedData<?> parse(KubeJSContext cx, @Nullable Object object) {
        switch (object) {
            case DataDrivenTypedData<?> typed -> {
                return typed;
            }
            case Map<?, ?> map -> {
                map = new HashMap<>(map);
                if (map.containsKey("type")) {
                    var type = Objects.requireNonNull(String.valueOf(map.remove("type")));
                    var data = switch (type) {
                        case "sequential" -> {
                            var values = (List<?>) Objects.requireNonNull(map.remove("values"));
                            var elseVal = map.remove("else");
                            var valuesData = new ArrayList<DataDrivenTypedData<?>>();
                            for (var value : values) {
                                var valueData = parse(cx, value);
                                valuesData.add(valueData);
                            }
                            if (elseVal != null) {
                                var reference = ((ItemStackRestrictionEntryJSImpl) elseVal).reference();
                                valuesData.add(new DataDrivenTypedData<>(ValueData.TYPE, new ValueData(reference)));
                            }

                            yield new SequentialData(valuesData);
                        }
                        case "predicate" -> {
                            var json = (JsonObject) cx.jsToJava(map.remove("condition"), JSON_TYPE);
                            var ops = RegistryOps.create(JsonOps.INSTANCE, Objects.requireNonNull(Objects
                                    .requireNonNull(cx.getRegistries())
                                    .access()));
                            var predicateResult = Objects.requireNonNull(ItemPredicate.CODEC.parse(ops, json));
                            if (predicateResult.isError()) throw new IllegalStateException(Objects
                                    .requireNonNull(predicateResult.error())
                                    .orElseThrow()
                                    .message());
                            var predicate = Objects.requireNonNull(predicateResult.result()).orElseThrow();
                            var reference = ((ItemStackRestrictionEntryJSImpl) Objects.requireNonNull(map.remove("return"))).reference();
                            yield new PredicateData(predicate, reference);
                        }
                        default -> throw new IllegalStateException("Unsupported type: " + type);
                    };
                    if (!map.isEmpty()) {
                        throw new IllegalArgumentException("Unknown keys " + map.keySet());
                    }
                    return new DataDrivenTypedData<>(type, data);
                } else {
                    throw new IllegalArgumentException("\"type\" must be specified");
                }
            }
            case ItemStackRestrictionEntryJSImpl e -> {
                return new DataDrivenTypedData<>("value", new ValueData(e.reference()));
            }
            case null, default -> throw new IllegalArgumentException("Unsupported input: " + object);
        }
    }
}