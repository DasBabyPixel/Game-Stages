package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.data.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface ResolverAlgorithm<CustomData, Fin> {
    @Nullable Fin resolve(ResolverAlgorithmData<CustomData, Fin> data, ItemStack itemStack);
}
