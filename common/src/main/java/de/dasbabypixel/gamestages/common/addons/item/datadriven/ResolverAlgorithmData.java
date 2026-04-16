package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.data.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface ResolverAlgorithmData<CustomData, Fin> {
    ResolverAlgorithm<CustomData, Fin> algorithm();

    CustomData customData();

    List<Fin> entries();

    default @Nullable Fin resolve(ItemStack itemStack) {
        return algorithm().resolve(this, itemStack);
    }
}
