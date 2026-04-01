package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jspecify.annotations.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface DataDrivenSerializer<Data extends DataDrivenData> {
    @NonNull Data deserialize(@NonNull RegistryFriendlyByteBuf buf);

    void serialize(@NonNull RegistryFriendlyByteBuf buf, @NonNull Data data);

    static <Data extends DataDrivenData> DataDrivenSerializer<Data> serializer(@NonNull BiConsumer<@NonNull Data, @NonNull RegistryFriendlyByteBuf> encode, @NonNull Function<@NonNull RegistryFriendlyByteBuf, @NonNull Data> decode) {
        return new DataDrivenSerializer<>() {
            @Override
            public @NonNull Data deserialize(@NonNull RegistryFriendlyByteBuf buf) {
                return decode.apply(buf);
            }

            @Override
            public void serialize(@NonNull RegistryFriendlyByteBuf buf, @NonNull Data data) {
                encode.accept(data, buf);
            }
        };
    }
}
