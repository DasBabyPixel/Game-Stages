package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types.NeoItemRestrictionEntry;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class RegisterEventJS implements KubeEvent {
    private final @NonNull RegistryAccessContainer registries;
    private final @NonNull AbstractGameStageManager stageManager;
    private final @NonNull Map<Set<String>, CommonItemCollection.Direct> collectionByModCache = new ConcurrentHashMap<>();

    public RegisterEventJS(@NonNull RegistryAccessContainer registries, @NonNull AbstractGameStageManager stageManager) {
        this.registries = registries;
        this.stageManager = stageManager;
    }

    public @NonNull GameStage registerStage(@NonNull String stageName) {
        var stage = new GameStage(stageName);
        stageManager.add(stage);
        return stage;
    }

    @SuppressWarnings("unchecked")
    public @NonNull CommonItemCollection<?> items(Context cx, Object... inputs) {
        var items = new ArrayList<Holder<Item>>();
        var tags = new ArrayList<TagKey<Item>>();
        var mods = new HashSet<String>();
        outer:
        for (var input : inputs) {
            while (true) {
                switch (input) {
                    case Wrapper wrapper -> {
                        input = wrapper.unwrap();
                        continue;
                    }
                    case ItemLike like -> items.add(BuiltInRegistries.ITEM.wrapAsHolder(like.asItem()));
                    case TagKey<?> tagKey -> tags.add((TagKey<Item>) tagKey);
                    case CharSequence sequence -> {
                        var string = sequence.toString();
                        if (string.startsWith("@")) {
                            // Mod
                            mods.add(string.substring(1));
                            continue outer;
                        }
                        if (string.startsWith("#")) {
                            // Tag
                            var tag = TagKey.create(Registries.ITEM, ResourceLocation.parse(string.substring(1)));
                            tags.add(tag);
                            continue outer;
                        }
                        if (string.startsWith(".")) {
                            items.add(BuiltInRegistries.ITEM
                                    .getHolder(ResourceLocation.parse(string.substring(1)))
                                    .orElseThrow());
                            continue outer;
                        }
                        items.add(BuiltInRegistries.ITEM.getHolder(ResourceLocation.parse(string)).orElseThrow());
                    }
                    case null, default ->
                            throw new KubeRuntimeException("Cannot parse: " + input).source(SourceLine.of(cx));
                }
                continue outer;
            }
        }
        var itemsCol = items.isEmpty() ? null : new CommonItemCollection.Direct(HolderSet.direct(items));
        var tagsCol = tags.isEmpty() ? null : new CommonItemCollection.Direct(HolderSet.direct(tags
                .stream()
                .map(BuiltInRegistries.ITEM::getTag)
                .map(Optional::orElseThrow)
                .flatMap(HolderSet::stream)
                .toList()));
        var modsCol = mods.isEmpty() ? null : itemsByModInternal(mods);
        var col = tagsCol == null ? itemsCol : (itemsCol == null ? tagsCol : itemsCol.union(tagsCol));
        col = modsCol == null ? col : (col == null ? modsCol : col.union(modsCol));
        return col == null ? new CommonItemCollection.Direct(HolderSet.empty()) : col;
    }

    private CommonItemCollection.Direct itemsByModInternal(Set<String> ids) {
        return collectionByModCache.computeIfAbsent(ids, id -> {
            var items = BuiltInRegistries.ITEM
                    .holders()
                    .filter(r -> ids.contains(r.key().location().getNamespace()))
                    .toList();
            var holderSet = HolderSet.direct(items);
            return new CommonItemCollection.Direct(holderSet);
        });
    }

    public @NonNull NeoItemRestrictionEntry register(@NonNull PreparedRestrictionPredicate predicate, @NonNull CommonItemCollection<? extends CommonItemCollection<?>> itemCollection) {
        return stageManager.addRestriction(new NeoItemRestrictionEntry(predicate, itemCollection));
    }
}
