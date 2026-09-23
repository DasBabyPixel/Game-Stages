package de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public abstract class VFluidAddon implements VAddon {
    private static @Nullable VFluidAddon instance;

    public VFluidAddon() {
        instance = this;
        REGISTER_CUSTOM_CONTENT_EVENT.addListener(this::handle);
        REGISTER_PACKETS_EVENT.addListener(this::handle);
    }

    public static VFluidAddon instance() {
        return Objects.requireNonNull(instance);
    }

    private void handle(RegisterCustomContentEvent event) {
        FluidType.register(event.contentRegistry());
    }

    private void handle(RegisterPacketsEvent event) {
        var registry = event.registry();
        registry.playClientBound(CommonFluidRestrictionPacket.TYPE, CommonFluidRestrictionPacket.STREAM_CODEC);
    }

    public abstract void handle(CommonFluidRestrictionPacket packet);
}
