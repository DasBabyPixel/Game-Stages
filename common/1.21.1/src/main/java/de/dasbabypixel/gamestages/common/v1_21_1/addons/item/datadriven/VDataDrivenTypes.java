package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential.SequentialData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.value.ValueData;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenSerializer;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenType;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Objects;

public class VDataDrivenTypes {
    public static void register(@NonNull DataDrivenTypes types, @NonNull ItemStackRestrictionResolverFactories factories) {
        var itemStackRestrictionEntryReference = new DataDrivenType<>(ItemStackRestrictionEntryReference.class, "itemstack_restriction_entry_reference", new DataDrivenSerializer<>() {
            @Override
            public @NonNull ItemStackRestrictionEntryReference deserialize(@NonNull RegistryFriendlyByteBuf buf) {
                return new ItemStackRestrictionEntryReference(buf.readUtf());
            }

            @Override
            public void serialize(@NonNull RegistryFriendlyByteBuf buf, @NonNull ItemStackRestrictionEntryReference data) {
                buf.writeUtf(data.referenceId());
            }
        });
        types.register(itemStackRestrictionEntryReference);
        types.register(new DataDrivenType<>(ValueData.class, "value_data", new DataDrivenSerializer<>() {
            @Override
            public @NonNull ValueData deserialize(@NonNull RegistryFriendlyByteBuf buf) {
                return new ValueData(itemStackRestrictionEntryReference.serializer().deserialize(buf));
            }

            @Override
            public void serialize(@NonNull RegistryFriendlyByteBuf buf, @NonNull ValueData data) {
                itemStackRestrictionEntryReference.serializer().serialize(buf, data.restrictionEntryReference());
            }
        }));
        types.register(new DataDrivenType<>(SequentialData.class, "sequential", new DataDrivenSerializer<>() {
            @Override
            public @NonNull SequentialData deserialize(@NonNull RegistryFriendlyByteBuf buf) {
                var count = buf.readVarInt();
                var list = new ArrayList<DataDrivenTypedData<?>>();
                for (var i = 0; i < count; i++) {
                    list.add(read(buf));
                }
                return new SequentialData(list);
            }

            private DataDrivenTypedData<?> read(@NonNull RegistryFriendlyByteBuf buf) {
                var type = buf.readUtf();
                var data = DataDrivenTypes.instance().get(type).serializer().deserialize(buf);
                return new DataDrivenTypedData<>(type, data);
            }

            @Override
            public void serialize(@NonNull RegistryFriendlyByteBuf buf, @NonNull SequentialData data) {
                buf.writeVarInt(data.values().size());
                for (var value : data.values()) {
                    write(buf, value);
                }
            }

            private <T extends DataDrivenData> void write(@NonNull RegistryFriendlyByteBuf buf, @NonNull DataDrivenTypedData<T> data) {
                buf.writeUtf(data.type());
                var serializer = DataDrivenTypes
                        .instance()
                        .get(data.type())
                        .unsafeCast(DataDrivenData.class)
                        .serializer();
                serializer.serialize(buf, Objects.requireNonNull(data.data()));
            }
        }));
    }
}
