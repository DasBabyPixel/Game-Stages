package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSParserBase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.ItemLike;

public class ItemJSParser extends JSParserBase {
    public ItemJSParser() {
        registerRegistryHandlers(ItemLike.class, BuiltInRegistries.ITEM, ItemLike::asItem, CommonItemCollection::new, CommonItemCollection.TYPE);
    }
}
