package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record DataDrivenType<B extends ByteBuf, Data extends DataDrivenData>(Class<Data> cls, String type,
                                                                             StreamCodec<B, Data> codec,
                                                                             StreamCodec<B, DataDrivenRTypedData<Data>> typedCodec) {
    @SuppressWarnings("DataFlowIssue")
    public DataDrivenType(Class<Data> cls, String type, StreamCodec<B, Data> codec) {
        this(cls, type, codec, StreamCodec.composite(DataDrivenNetwork.DATA_DRIVEN_TYPE_STREAM_CODEC, DataDrivenRTypedData::type, codec, DataDrivenRTypedData::data, DataDrivenRTypedData::fromTypedData));
    }

    @SuppressWarnings("unchecked")
    public <D extends DataDrivenData> DataDrivenType<B, D> cast(Class<D> cls) {
        if (this.cls != cls) throw new IllegalStateException();
        return (DataDrivenType<B, D>) this;
    }

    @SuppressWarnings("unchecked")
    public <D extends DataDrivenData> DataDrivenType<B, D> unsafeCast(Class<D> cls) {
        return (DataDrivenType<B, D>) this;
    }
}
