package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonFluidCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollection;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.fluid.FluidLike;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

class JSParser {
    private final @NonNull Map<Registry<?>, Map<String, HolderSet<?>>> modCache = new ConcurrentHashMap<>();

    public GameContent parseMods(Context cx, String... mods) {
        var items = parseItems(cx, Arrays.stream(mods).map("@"::concat).toArray());
        var fluids = parseFluids(cx, Arrays.stream(mods).map("@"::concat).toArray());
        return items.union(fluids);
    }

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
            content.add(mods(CommonItemCollection.EMPTY, BuiltInRegistries.ITEM, mods, CommonItemCollection::new));
        }
        if (content.isEmpty()) return CommonItemCollection.EMPTY;
        return CommonItemCollection.EMPTY.union(content.toArray(GameContent[]::new));
    }

    @SuppressWarnings("unchecked")
    public GameContent parseFluids(Context cx, Object... inputs) {
        var parseQueue = new ArrayDeque<>(Arrays.asList(inputs));
        var fluids = new ArrayList<Holder<Fluid>>();
        var tags = new ArrayList<TagKey<Fluid>>();
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
                    case FluidLike like -> fluids.add(BuiltInRegistries.FLUID.wrapAsHolder(like.kjs$getFluid()));
                    case TagKey<?> tagKey -> tags.add((TagKey<Fluid>) tagKey);
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
                            var tag = TagKey.create(Registries.FLUID, ResourceLocation.parse(string.substring(1)));
                            tags.add(tag);
                            continue outer;
                        }
                        if (string.startsWith(".")) {
                            // Fluid
                            string = string.substring(1);
                            var opt = BuiltInRegistries.FLUID.getHolder(ResourceLocation.parse(string));
                            if (opt.isPresent()) fluids.add(opt.orElseThrow());
                            else throw new NoSuchElementException("No fluid named `" + string + "` found");
                            continue outer;
                        }
                        var opt = BuiltInRegistries.FLUID.getHolder(ResourceLocation.parse(string));
                        if (opt.isPresent()) fluids.add(opt.orElseThrow());
                        else throw new NoSuchElementException("No fluid named `" + string + "` found");
                    }
                    case null, default ->
                            throw new KubeRuntimeException("Cannot parse: " + input).source(SourceLine.of(cx));
                }
                continue outer;
            }
        }
        if (!fluids.isEmpty()) {
            content.add(new CommonFluidCollection(HolderSet.direct(fluids)));
        }
        if (!tags.isEmpty()) {
            content.add(new CommonFluidCollection(HolderSet.direct(tags
                    .stream()
                    .map(BuiltInRegistries.FLUID::getTag)
                    .map(Optional::orElseThrow)
                    .flatMap(HolderSet::stream)
                    .toList())));
        }
        if (!mods.isEmpty()) {
            content.add(mods(CommonFluidCollection.EMPTY, BuiltInRegistries.FLUID, mods, CommonFluidCollection::new));
        }
        if (content.isEmpty()) return CommonFluidCollection.EMPTY;
        return CommonFluidCollection.EMPTY.union(content.toArray(GameContent[]::new));
    }

    private <C extends GameContent, T> GameContent mods(C base, Registry<T> registry, Collection<String> mods, Function<HolderSet<T>, C> gen) {
        var list = new ArrayList<GameContent>(mods.size());
        for (var mod : mods) {
            var g = gen.apply(mod(registry, mod));
            list.add(g);
        }
        return base.union(list.toArray(GameContent[]::new));
    }

    @SuppressWarnings("unchecked")
    private <T> HolderSet<T> mod(Registry<T> registry, String mod) {
        var map = modCache.computeIfAbsent(registry, ignored -> new ConcurrentHashMap<>());
        if (map.containsKey(mod)) return (HolderSet<T>) map.get(mod);
        var refs = registry.holders().filter(r -> mod.equals(r.key().location().getNamespace())).toList();
        var hs = HolderSet.direct(refs);
        map.put(mod, hs);
        return hs;
    }
}
