package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollection;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
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

class JSParser {
    private final @NonNull Map<Set<String>, CommonItemCollection> collectionByModCache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public GameContent parseItems(Context cx, Object... inputs) {
        var parseQueue = new ArrayDeque<>(Arrays.asList(inputs));
        var items = new ArrayList<Holder<Item>>();
        var tags = new ArrayList<TagKey<Item>>();
        var mods = new HashSet<String>();
        var content = new ArrayList<GameContent>();
        outer:
        for (var input = parseQueue.poll(); input != null; input = parseQueue.poll()) {
            while (true) {
                switch (input) {
                    case Wrapper wrapper -> {
                        input = wrapper.unwrap();
                        continue;
                    }
                    case ItemLike like -> items.add(BuiltInRegistries.ITEM.wrapAsHolder(like.asItem()));
                    case TagKey<?> tagKey -> tags.add((TagKey<Item>) tagKey);
                    case GameContent c -> content.add(c);
                    case Collection<?> list -> parseQueue.addAll(list);
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
                            // Item
                            string = string.substring(1);
                            var opt = BuiltInRegistries.ITEM.getHolder(ResourceLocation.parse(string));
                            if (opt.isPresent()) items.add(opt.orElseThrow());
                            else throw new NoSuchElementException("No item named `" + string + "` found");
                            continue outer;
                        }
                        var opt = BuiltInRegistries.ITEM.getHolder(ResourceLocation.parse(string));
                        if (opt.isPresent()) items.add(opt.orElseThrow());
                        else throw new NoSuchElementException("No item named `" + string + "` found");
                    }
                    case null, default ->
                            throw new KubeRuntimeException("Cannot parse: " + input).source(SourceLine.of(cx));
                }
                continue outer;
            }
        }
        if (!items.isEmpty()) {
            content.add(new CommonItemCollection(HolderSet.direct(items)));
        }
        if (!tags.isEmpty()) {
            content.add(new CommonItemCollection(HolderSet.direct(tags
                    .stream()
                    .map(BuiltInRegistries.ITEM::getTag)
                    .map(Optional::orElseThrow)
                    .flatMap(HolderSet::stream)
                    .toList())));
        }
        if (!mods.isEmpty()) {
            content.add(itemsByModInternal(mods));
        }
        if (content.isEmpty()) return CommonItemCollection.EMPTY;
        return CommonItemCollection.EMPTY.union(content.toArray(GameContent[]::new));
    }

    private CommonItemCollection itemsByModInternal(Set<String> ids) {
        return collectionByModCache.computeIfAbsent(ids, id -> {
            var items = BuiltInRegistries.ITEM
                    .holders()
                    .filter(r -> ids.contains(r.key().location().getNamespace()))
                    .toList();
            var holderSet = HolderSet.direct(items);
            return new CommonItemCollection(holderSet);
        });
    }
}
