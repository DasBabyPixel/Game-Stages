package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class DataDrivenTypes {
    private static final DataDrivenTypes INSTANCE = new DataDrivenTypes();
    private final Map<String, DataDrivenType<?, ?>> types = new HashMap<>();

    private DataDrivenTypes() {
    }

    public <B extends ByteBuf, T extends DataDrivenData> DataDrivenType<B, T> register(DataDrivenType<B, T> type) {
        this.types.put(type.type(), type);
        return type;
    }

    public DataDrivenType<?, ?> get(String type) {
        return Objects.requireNonNull(types.get(type), () -> "Missing '" + type + "' as data driven type");
    }

    public static DataDrivenTypes instance() {
        return INSTANCE;
    }
}
