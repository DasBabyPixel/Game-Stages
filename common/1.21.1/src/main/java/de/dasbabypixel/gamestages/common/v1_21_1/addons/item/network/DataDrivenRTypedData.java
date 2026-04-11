package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record DataDrivenRTypedData<Data extends DataDrivenData>(DataDrivenType<? extends ByteBuf, Data> type,
                                                                Data data) {
    public DataDrivenTypedData<Data> toTypedData() {
        return new DataDrivenTypedData<>(type.type(), data);
    }

    public static <Data extends DataDrivenData> DataDrivenRTypedData<Data> fromTypedData(String type, Data data) {
        return fromTypedData(DataDrivenTypes.instance().get(type), data);
    }

    @SuppressWarnings("unchecked")
    public static <Data extends DataDrivenData> DataDrivenRTypedData<Data> fromTypedData(DataDrivenType<?, ?> type, Data data) {
        var t = (DataDrivenType<? extends @NonNull ByteBuf, Data>) type;
        return new DataDrivenRTypedData<>(t, data);
    }

    public static <Data extends DataDrivenData> DataDrivenRTypedData<Data> fromTypedData(DataDrivenTypedData<Data> data) {
        return fromTypedData(data.type(), data.data());
    }
}
