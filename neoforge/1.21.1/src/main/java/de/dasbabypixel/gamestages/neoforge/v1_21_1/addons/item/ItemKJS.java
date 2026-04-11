package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential.SequentialData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.value.ValueData;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.VItemStackRestrictionEntrySettings;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.predicate.PredicateData;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenTypes;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.EventRegistry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonKJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventJSBase;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.RegisterEventJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.RegistryOps;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class ItemKJS implements NeoAddonKJS {
    private static final TypeInfo JSON_TYPE = Objects.requireNonNull(TypeInfo.of(JsonElement.class));
    private static final TypeInfo RESTRICTION_ENTRY_REFERENCE_TYPE = Objects.requireNonNull(TypeInfo.of(ItemStackRestrictionEntryReference.class));
    private final ItemJSParser itemParser = new ItemJSParser();

    @Override
    public void registerEventExtensions(EventRegistry registry) {
        var type = registry.get(RegisterEventJS.class);
        type.addFunctionVarArgs("items", (event, cx, args) -> args[0], ItemCollectionWrapper.class, ItemCollectionWrapper.class, ItemCollectionWrapper[].class);
        type.addFunctionVarArgs("restrictItems", this::restrictItems, ItemCollectionWrapper.class, CommonItemRestrictionEntry.class, PreparedRestrictionPredicate.class, ItemCollectionWrapper[].class);
        type.addFunction("registerItemStackEntry", e -> new RegisteredItemStackEntries(), new EventJSBase.ContextFunction<RegisterEventJS, RegisteredItemStackEntries>() {
            @Override
            public Object invoke(RegisterEventJS event, KubeJSContext cx, @Nullable RegisteredItemStackEntries s, Object[] args) {
                Objects.requireNonNull(s);
                var predicate = (PreparedRestrictionPredicate) Objects.requireNonNull(args[0]);
                var compilationContext = event.stageManager().get(ItemAddon.STAGE_MANAGER_CONTEXT);
                var settings = VItemStackRestrictionEntrySettings.instance();
                var restrictionEntry = new ItemStackRestrictionEntry(predicate, settings);
                var reference = compilationContext.addRestrictionEntry(restrictionEntry);

                var entry = new RegisteredItemStackEntries.Entry(reference, predicate, settings);
                s.entries.add(entry);
                return entry;
            }

            @Override
            public void finish(RegisterEventJS event, @Nullable RegisteredItemStackEntries registeredItemStackEntries) {
                Objects.requireNonNull(registeredItemStackEntries);
                for (var entry : registeredItemStackEntries.entries()) {
                    var predicate = entry.predicate;
                    var settings = entry.settings;
                }
            }
        }, RegisteredItemStackEntries.Entry.class, PreparedRestrictionPredicate.class);
        type.addFunctionVarArgs("restrictItemStacks", RestrictContext::new, new EventJSBase.ContextFunction<>() {
            @Override
            public Object invoke(RegisterEventJS event, KubeJSContext cx, @Nullable RestrictContext restrictContext, Object[] args) {
                var origin = RestrictionEntryOrigin.string(Objects.requireNonNull(SourceLine.of(cx)).toString());
                var data = (DataDrivenTypedData<?>) Objects.requireNonNull(args[0]);
                var items = ((ItemCollectionWrapper) Objects.requireNonNull(args[1])).content();
                var dataDrivenType = DataDrivenTypes.instance().get(data.type()).unsafeCast();
                var factoryId = "data_driven";

                var networkData = new DataDrivenNetwork.NetworkData<DataDrivenData<?, ?>>(dataDrivenType, Objects.requireNonNull(data.data()), factoryId);
                var entry = new CommonItemRestrictionEntry(origin, items, networkData);
                return event.stageManager().addRestriction(entry);
            }

            @Override
            public void finish(RegisterEventJS event, @Nullable RestrictContext restrictContext) {
                EventJSBase.ContextFunction.super.finish(event, restrictContext);
            }
        }, ItemCollectionWrapper.class, void.class, DataDrivenTypedData.class, ItemCollectionWrapper[].class);
    }

    private CommonItemRestrictionEntry restrictItems(RegisterEventJS event, KubeJSContext cx, Object[] args) {
        return restrictItems(event, cx, (PreparedRestrictionPredicate) args[0], ((ItemCollectionWrapper) Objects.requireNonNull(args[1])).content());
    }

    private CommonItemRestrictionEntry restrictItems(RegisterEventJS event, KubeJSContext cx, PreparedRestrictionPredicate predicate, GameContent itemsContent) {
        var origin = RestrictionEntryOrigin.string(Objects.requireNonNull(SourceLine.of(cx)).toString());
        var dataDrivenType = DataDrivenTypes.instance().get(ValueData.TYPE).unsafeCast();
        var factoryId = "data_driven";
        var itemStackSettings = VItemStackRestrictionEntrySettings.instance();
        var itemStackRestrictionEntry = new ItemStackRestrictionEntry(predicate, itemStackSettings);
        var reference = event
                .stageManager()
                .get(ItemAddon.STAGE_MANAGER_CONTEXT)
                .addRestrictionEntry(itemStackRestrictionEntry);
        var data = new ValueData(reference);

        var networkData = new DataDrivenNetwork.NetworkData<>(dataDrivenType, data, factoryId);
        var entry = new CommonItemRestrictionEntry(origin, itemsContent, networkData);
        return event.stageManager().addRestriction(entry);
    }

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry registry) {
        registry.register(ItemCollectionWrapper.class, (TypeWrapperRegistry.ContextFromFunction<ItemCollectionWrapper>) (context, o) -> new ItemCollectionWrapper(itemParser.parse(Objects.requireNonNull(context), Objects.requireNonNull(o))));
        registry.register(DataDrivenTypedData.class, (context, o, typeInfo) -> parse((KubeJSContext) context, o));
        registry.register(ItemStackRestrictionEntryReference.class, (context, o, typeInfo) -> switch (o) {
            case ItemStackRestrictionEntryReference ref -> ref;
            case RegisteredItemStackEntries.Entry e -> e.reference;
            case null, default -> throw new IllegalStateException("Unexpected value: " + o);
        });
    }

    private DataDrivenTypedData<?> parse(KubeJSContext cx, @Nullable Object object) {
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
                            var reference = (ItemStackRestrictionEntryReference) Objects.requireNonNull(cx.jsToJava(elseVal, RESTRICTION_ENTRY_REFERENCE_TYPE));
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
                        var reference = (ItemStackRestrictionEntryReference) Objects.requireNonNull(cx.jsToJava(map.remove("return"), RESTRICTION_ENTRY_REFERENCE_TYPE));
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
        throw new IllegalArgumentException("Unsupported input: " + object);
    }

    private static class RestrictContext {
        private final RegisterEventJS event;

        public RestrictContext(RegisterEventJS event) {
            this.event = event;
        }
    }

    public static class RegisteredItemStackEntries {
        private final List<Entry> entries = new ArrayList<>();

        public List<Entry> entries() {
            return entries;
        }

        public static class Entry {
            private final ItemStackRestrictionEntryReference reference;
            private final PreparedRestrictionPredicate predicate;
            private final VItemStackRestrictionEntrySettings settings;

            public Entry(ItemStackRestrictionEntryReference reference, PreparedRestrictionPredicate predicate, VItemStackRestrictionEntrySettings settings) {
                this.reference = reference;
                this.predicate = predicate;
                this.settings = settings;
            }

            public VItemStackRestrictionEntrySettings settings() {
                return settings;
            }
        }
    }
}