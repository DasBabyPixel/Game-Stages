package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential.SequentialData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.value.ValueData;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.VItemStackRestrictionEntrySettings;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.predicate.PredicateData;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

import static de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs.PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC;

@NullMarked
public class DataDrivenNetwork {
    @SuppressWarnings("DataFlowIssue")
    public static final StreamCodec<ByteBuf, DataDrivenType<?, ?>> DATA_DRIVEN_TYPE_STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(DataDrivenTypes.instance()::get, DataDrivenType::type);
    @SuppressWarnings({"DataFlowIssue", "unchecked"})
    public static final StreamCodec<RegistryFriendlyByteBuf, DataDrivenRTypedData<?>> DATA_DRIVEN_R_TYPED_DATA_STREAM_CODEC = DATA_DRIVEN_TYPE_STREAM_CODEC
            .<RegistryFriendlyByteBuf>cast()
            .dispatch(DataDrivenRTypedData::type, type -> (StreamCodec<? super RegistryFriendlyByteBuf, ? extends DataDrivenRTypedData<?>>) type.typedCodec());
    @SuppressWarnings("DataFlowIssue")
    public static final StreamCodec<RegistryFriendlyByteBuf, NetworkData<?>> NETWORK_DATA_STREAM_CODEC = StreamCodec.composite(DATA_DRIVEN_R_TYPED_DATA_STREAM_CODEC, NetworkData::data, ByteBufCodecs.STRING_UTF8, NetworkData::factoryId, NetworkData::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, DataDrivenTypedData<?>> DATA_DRIVEN_TYPED_DATA_STREAM_CODEC = DATA_DRIVEN_R_TYPED_DATA_STREAM_CODEC.map(DataDrivenRTypedData::toTypedData, r -> {
        var type = DataDrivenTypes.instance().get(r.type()).unsafeCast();
        return new DataDrivenRTypedData<>(type, r.data());
    });
    @SuppressWarnings("DataFlowIssue")
    public static final StreamCodec<RegistryFriendlyByteBuf, SequentialData> SEQUENTIAL_DATA_STREAM_CODEC = DATA_DRIVEN_TYPED_DATA_STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(SequentialData::new, SequentialData::values);
    @SuppressWarnings("DataFlowIssue")
    public static final StreamCodec<ByteBuf, ItemStackRestrictionEntryReference> ITEM_STACK_RESTRICTION_ENTRY_REFERENCE_STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(ItemStackRestrictionEntryReference::new, ItemStackRestrictionEntryReference::referenceId);
    @SuppressWarnings("DataFlowIssue")
    public static final StreamCodec<ByteBuf, ValueData> VALUE_DATA_STREAM_CODEC = ITEM_STACK_RESTRICTION_ENTRY_REFERENCE_STREAM_CODEC.map(ValueData::new, ValueData::restrictionEntryReference);
    @SuppressWarnings("DataFlowIssue")
    public static final StreamCodec<RegistryFriendlyByteBuf, PredicateData> PREDICATE_DATA_STREAM_CODEC = StreamCodec.composite(fromCodec(ItemPredicate.CODEC), PredicateData::predicate, ITEM_STACK_RESTRICTION_ENTRY_REFERENCE_STREAM_CODEC, PredicateData::resultReference, PredicateData::new);
    public static final StreamCodec<ByteBuf, VItemStackRestrictionEntrySettings> V_ITEM_STACK_RESTRICTION_ENTRY_SETTINGS_STREAM_CODEC = StreamCodec.unit(VItemStackRestrictionEntrySettings.instance());
    @SuppressWarnings("DataFlowIssue")
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackRestrictionEntry> ITEM_STACK_RESTRICTION_ENTRY_STREAM_CODEC = StreamCodec.composite(PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC, ItemStackRestrictionEntry::predicate, V_ITEM_STACK_RESTRICTION_ENTRY_SETTINGS_STREAM_CODEC, e -> (VItemStackRestrictionEntrySettings) e.settings(), ItemStackRestrictionEntry::new);

    @SuppressWarnings("SameParameterValue")
    private static <V> StreamCodec<RegistryFriendlyByteBuf, V> fromCodec(Codec<V> codec) {
        return StreamCodec.of((o, v) -> {
            var ops = RegistryOps.create(NbtOps.INSTANCE, o.registryAccess());
            DataResult<Tag> tagResult = codec.encodeStart(ops, v);
            var tag = tagResult.result().orElseThrow();
            ByteBufCodecs.TAG.encode(o, tag);
        }, o -> {
            var tag = ByteBufCodecs.TAG.decode(o);
            var result = codec.decode(RegistryOps.create(NbtOps.INSTANCE, o.registryAccess()), tag);
            return Objects.requireNonNull(result.result().orElseThrow().getFirst());
        });
    }

    public record NetworkData<Data extends DataDrivenData<?, ?>>(DataDrivenRTypedData<Data> data, String factoryId) {
        public NetworkData(DataDrivenType<?, Data> type, Data data, String factoryId) {
            this(new DataDrivenRTypedData<>(type, data), factoryId);
        }
    }
}
