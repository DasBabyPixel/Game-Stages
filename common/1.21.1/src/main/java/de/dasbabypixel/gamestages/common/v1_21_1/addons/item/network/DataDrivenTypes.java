package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network;

import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataDrivenTypes {
    private static final @NonNull DataDrivenTypes INSTANCE = new DataDrivenTypes();
    private final Map<String, DataDrivenType<?>> types = new HashMap<>();

    private DataDrivenTypes() {
    }

    public void register(@NonNull DataDrivenType<?> type) {
        this.types.put(type.type(), type);
    }

    public @NonNull DataDrivenType<?> get(String type) {
        return Objects.requireNonNull(types.get(type));
    }

    public static @NonNull DataDrivenTypes instance() {
        return INSTANCE;
    }
}
