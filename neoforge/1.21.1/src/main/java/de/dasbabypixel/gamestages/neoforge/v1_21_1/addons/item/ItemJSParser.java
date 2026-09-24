package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.ItemType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSContext;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSParserBase;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.TypedGameCollectionJS;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ItemJSParser extends JSParserBase {
    public ItemJSParser() {
        registerRegistryHandlers(ItemLike.class, Registries.ITEM, ItemLike::asItem, ItemType.get());
    }

    @Override
    public TypedGameCollectionJS parse(Context cx, Object @Nullable ... inputs) {
        var c = super.parse(cx, inputs);
        var type = JSContext.instance(cx).get(JSContext.Attributes.CONTENT_TYPES).type(ItemType.get());
        return c.filterType(type);
    }
}
