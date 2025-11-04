package de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.ItemCollection;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager.Attribute;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollection;
import net.minecraft.core.HolderSet;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ItemCollectionCompiler {
    public static final Attribute<ItemCollectionCompiler> ATTRIBUTE = new Attribute<>(ItemCollectionCompiler::new);
    private final Map<CommonItemCollection<?>, CommonItemCollection.Direct> cache = new HashMap<>();

    private ItemCollectionCompiler() {
    }

    public CommonItemCollection.Direct flatten(ItemCollection<?> itemCollection) {
        if (!(itemCollection instanceof CommonItemCollection<?> commonItemCollection))
            throw new IllegalArgumentException("Not a CommonItemCollection");
        if (cache.containsKey(commonItemCollection)) {
            return cache.get(commonItemCollection);
        }
        var compiled = switch (commonItemCollection) {
            case CommonItemCollection.Direct direct -> direct;
            case CommonItemCollection.Except(var base, var exclusion) -> {
                var directBase = flatten(base);
                var directExclusion = flatten(exclusion);
                var exclude = directExclusion.items();
                var items = HolderSet.direct(directBase.items().stream().filter(h -> !exclude.contains(h)).toList());
                yield new CommonItemCollection.Direct(items);
            }
            case CommonItemCollection.Union(var c1, var c2) -> {
                var directC1 = flatten(c1);
                var directC2 = flatten(c2);
                var excludeFromC1 = directC2.items();
                var filteredC1 = directC1.items().stream().filter(h -> !excludeFromC1.contains(h));
                var items = HolderSet.direct(Stream.concat(filteredC1, directC2.items().stream()).toList());
                yield new CommonItemCollection.Direct(items);
            }
        };
        if (commonItemCollection != compiled) {
            cache.put(commonItemCollection, compiled);
        }
        return compiled;
    }
}
