package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential.SequentialData;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.predicate.PredicateData;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenTypes;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.EventRegistry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonKJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.datadriven.NeoItemStackRestrictionEntrySettings;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventJSBase;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.RegisterEventJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.advancements.critereon.ItemPredicate;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class ItemKJS implements NeoAddonKJS {
    private static final TypeInfo JSON_TYPE = TypeInfo.of(JsonElement.class);
    private final ItemJSParser itemParser = new ItemJSParser();

    @Override
    public void registerEventExtensions(@NonNull EventRegistry registry) {
        var type = registry.get(RegisterEventJS.class);
        type.addFunctionVarArgs("items", (event, cx, args) -> args[0], ItemCollectionWrapper.class, ItemCollectionWrapper.class, ItemCollectionWrapper[].class);
        type.addFunctionVarArgs("restrictItems", this::restrictItems, ItemCollectionWrapper.class, NeoItemRestrictionEntry.class, PreparedRestrictionPredicate.class, ItemCollectionWrapper[].class);
        type.addFunction("registerItemStackEntry", e -> new RegisteredItemStackEntries(), new EventJSBase.ContextFunction<RegisterEventJS, RegisteredItemStackEntries>() {
            @Override
            public @NonNull Object invoke(@NonNull RegisterEventJS event, @NonNull KubeJSContext cx, @NonNull RegisteredItemStackEntries s, Object @NonNull [] args) {
                var predicate = (PreparedRestrictionPredicate) Objects.requireNonNull(args[0]);
                var entry = new RegisteredItemStackEntries.Entry(s.id++, predicate);
                s.entries.add(entry);
                return entry;
            }

            @Override
            public void finish(@NonNull RegisterEventJS event, @NonNull RegisteredItemStackEntries registeredItemStackEntries) {
                var compilationContext = event.stageManager().get(ItemAddon.STAGE_MANAGER_CONTEXT);
                for (var entry : registeredItemStackEntries.entries()) {
                    var referenceId = entry.str();
                    var predicate = entry.predicate;
                    var settings = entry.settings;
                    compilationContext.addRestrictionEntry(new ItemStackRestrictionEntryReference(referenceId), new ItemStackRestrictionEntry(predicate, settings));
                }
            }
        }, ItemStackRestrictionEntry.class, PreparedRestrictionPredicate.class);
        type.addFunction("test", TestContext::new, new EventJSBase.ContextFunction<>() {
            @Override
            public @Nullable Object invoke(@NonNull RegisterEventJS event, @NonNull KubeJSContext cx, @NonNull TestContext s, Object @NonNull [] args) {
                var data = (DataDrivenTypedData<?>) args[0];
                s.dataList.add(data);
                System.out.println("Data " + data);
                return null;
            }

            @Override
            public void finish(@NonNull RegisterEventJS event, @NonNull TestContext s) {
                System.out.println("Finish");
                System.out.println("Finish");
                System.out.println("Finish");
                System.out.println("Finish");
                System.out.println(s.dataList);
            }
        }, void.class, DataDrivenTypedData.class);
    }

    private NeoItemRestrictionEntry restrictItems(@NonNull RegisterEventJS event, @NonNull KubeJSContext cx, Object @NonNull [] args) {
        return restrictItems(event, cx, (PreparedRestrictionPredicate) args[0], ((ItemCollectionWrapper) Objects.requireNonNull(args[1])).content());
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
        registry.register(ItemCollectionWrapper.class, (TypeWrapperRegistry.ContextFromFunction<ItemCollectionWrapper>) (context, o) -> new ItemCollectionWrapper(itemParser.parse(Objects.requireNonNull(context), Objects.requireNonNull(o))));
        registry.register(DataDrivenTypedData.class, (context, o, typeInfo) -> parse((KubeJSContext) context, o));
    }

    private DataDrivenTypedData<?> parse(@NonNull KubeJSContext cx, Object object) {
        if (object instanceof DataDrivenTypedData<?> typed) return typed;
        if (object instanceof Map<?, ?> map) {
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
                        }
                        System.out.println("Else");
                        System.out.println(elseVal == null ? null : elseVal.getClass());
                        System.out.println(elseVal);

                        yield new SequentialData(valuesData);
                    }
                    case "predicate" -> {
                        var json = (JsonObject) cx.jsToJava(map.remove("condition"), JSON_TYPE);
                        System.out.println(json);
                        var predicateResult = Objects.requireNonNull(ItemPredicate.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, json)));
                        if (predicateResult.isError()) throw new IllegalStateException(Objects
                                .requireNonNull(predicateResult.error())
                                .orElseThrow()
                                .message());
                        var predicate = Objects.requireNonNull(predicateResult.result()).orElseThrow();
                        System.out.println(predicate);
                        var result = new ItemStackRestrictionEntryReference(((RegisteredItemStackEntries.Entry) Objects.requireNonNull(map.remove("return"))).str());
                        yield new PredicateData(predicate, result);
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
        throw new IllegalArgumentException("Unsupported input: " + object);
    }

    private static class TestContext {
        private final RegisterEventJS event;
        private final List<DataDrivenTypedData<?>> dataList = new ArrayList<>();

        public TestContext(RegisterEventJS event) {
            this.event = event;
        }
    }

    public static class RegisteredItemStackEntries {
        private final @NonNull List<Entry> entries = new ArrayList<>();
        private int id = 0;

        public @NonNull List<@NonNull Entry> entries() {
            return entries;
        }

        public static class Entry {
            private final int id;
            private final @NonNull PreparedRestrictionPredicate predicate;
            private final @NonNull NeoItemStackRestrictionEntrySettings settings = new NeoItemStackRestrictionEntrySettings();

            public Entry(int id, @NonNull PreparedRestrictionPredicate predicate) {
                this.id = id;
                this.predicate = predicate;
            }

            public @NonNull String str() {
                return "gen_" + id;
            }
        }
    }
}