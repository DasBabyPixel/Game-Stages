package de.dasbabypixel.gamestages.common.v1_21_1.data;

import de.dasbabypixel.gamestages.common.data.GameContent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface GameContentSerializer<T extends GameContent> {
    StreamCodec<? super RegistryFriendlyByteBuf, ? extends T> streamCodec();
}
