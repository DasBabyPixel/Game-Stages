package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import org.jspecify.annotations.NonNull;

public record DataDrivenType<Data extends DataDrivenData>(@NonNull Class<Data> cls, @NonNull String type,
                                                          @NonNull DataDrivenSerializer<Data> serializer) {
    @SuppressWarnings("unchecked")
    public <D extends DataDrivenData> DataDrivenType<D> cast(@NonNull Class<D> cls) {
        if (this.cls != cls) throw new IllegalStateException();
        return (DataDrivenType<D>) this;
    }

    @SuppressWarnings("unchecked")
    public <D extends DataDrivenData> DataDrivenType<D> unsafeCast(@NonNull Class<D> cls) {
        return (DataDrivenType<D>) this;
    }
}
