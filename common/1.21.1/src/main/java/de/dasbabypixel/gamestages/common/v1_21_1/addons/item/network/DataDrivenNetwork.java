package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.data.SequentialData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.data.ValueData;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.settings.VItemStackRestrictionEntrySettings;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NullMarked;

import static de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs.PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC;

@SuppressWarnings({"DataFlowIssue", "unchecked"})
@NullMarked
public class DataDrivenNetwork {
    public static final StreamCodec<RegistryFriendlyByteBuf, DataDrivenTypedData<?>> DATA_DRIVEN_TYPED_DATA_STREAM_CODEC = DataDrivenRTypedData.STREAM_CODEC.map(DataDrivenRTypedData::toTypedData, r -> {
        var type = DataDrivenTypes.instance().get(r.type()).unsafeCast();
        return new DataDrivenRTypedData<>(type, r.data());
    });
    public static final StreamCodec<RegistryFriendlyByteBuf, SequentialData> SEQUENTIAL_DATA_STREAM_CODEC = DATA_DRIVEN_TYPED_DATA_STREAM_CODEC.apply(ByteBufCodecs.list())
            .map(SequentialData::new, SequentialData::values);
    public static final StreamCodec<ByteBuf, ItemStackRestrictionEntryReference> ITEM_STACK_RESTRICTION_ENTRY_REFERENCE_STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(ItemStackRestrictionEntryReference::new, ItemStackRestrictionEntryReference::referenceId);
    public static final StreamCodec<ByteBuf, ValueData> VALUE_DATA_STREAM_CODEC = ITEM_STACK_RESTRICTION_ENTRY_REFERENCE_STREAM_CODEC.map(ValueData::new, ValueData::restrictionEntryReference);

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackRestrictionEntry> ITEM_STACK_RESTRICTION_ENTRY_STREAM_CODEC = StreamCodec.composite(PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC, ItemStackRestrictionEntry::predicate, VItemStackRestrictionEntrySettings.STREAM_CODEC, e -> (VItemStackRestrictionEntrySettings) e.settings(), ItemStackRestrictionEntry::new);

    public record NetworkData<Data extends DataDrivenData<?, ?>>(DataDrivenRTypedData<Data> data, String factoryId) {
        public static final StreamCodec<RegistryFriendlyByteBuf, NetworkData<?>> STREAM_CODEC = StreamCodec.composite(DataDrivenRTypedData.STREAM_CODEC, NetworkData::data, ByteBufCodecs.STRING_UTF8, NetworkData::factoryId, NetworkData::new);

        public NetworkData(DataDrivenType<?, Data> type, Data data, String factoryId) {
            this(new DataDrivenRTypedData<>(type, data), factoryId);
        }
    }
}
