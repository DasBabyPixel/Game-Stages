package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class DataDrivenNetwork {
    public static final StreamCodec<RegistryFriendlyByteBuf, DataDrivenType<?>> DATA_TYPE = new StreamCodec<>() {
        @Override
        public @NonNull DataDrivenType<?> decode(@NonNull RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            var type = registryFriendlyByteBuf.readUtf();
            return Objects.requireNonNull(DataDrivenTypes.instance().get(type));
        }

        @Override
        public void encode(@NonNull RegistryFriendlyByteBuf o, @NonNull DataDrivenType<?> dataDrivenType) {
            o.writeUtf(dataDrivenType.type());
        }
    };
    public static final StreamCodec<RegistryFriendlyByteBuf, NetworkData<?>> DATA_CODEC = new StreamCodec<>() {
        @Override
        public @NonNull NetworkData<?> decode(@NonNull RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            var type = DATA_TYPE.decode(registryFriendlyByteBuf);
            return decode(type, registryFriendlyByteBuf);
        }

        private <Data extends DataDrivenData> NetworkData<Data> decode(@NonNull DataDrivenType<Data> type, @NonNull RegistryFriendlyByteBuf buf) {
            return new NetworkData<>(type, type.serializer().deserialize(buf), buf.readUtf());
        }

        @Override
        public void encode(@NonNull RegistryFriendlyByteBuf o, @NonNull NetworkData<?> networkData) {
            DATA_TYPE.encode(o, networkData.type());
            encodeData(o, networkData);
            o.writeUtf(networkData.factoryId());
        }

        private <Data extends DataDrivenData> void encodeData(@NonNull RegistryFriendlyByteBuf buf, @NonNull NetworkData<Data> networkData) {
            networkData.type().serializer().serialize(buf, networkData.data());
        }
    };

    public record NetworkData<Data extends DataDrivenData>(@NonNull DataDrivenType<Data> type, Data data,
                                                           @NonNull String factoryId) {
    }
}
