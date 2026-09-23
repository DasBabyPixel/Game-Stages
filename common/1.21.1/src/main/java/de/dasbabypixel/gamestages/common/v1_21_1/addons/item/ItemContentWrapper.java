package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.data.GameContentDirect;
import de.dasbabypixel.gamestages.common.data.GameContentWrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record ItemContentWrapper(
        GameContentDirect<ItemType.ItemData, HolderSet<Item>, Holder<Item>> gameContent) implements GameContentWrapper.Direct {
}
