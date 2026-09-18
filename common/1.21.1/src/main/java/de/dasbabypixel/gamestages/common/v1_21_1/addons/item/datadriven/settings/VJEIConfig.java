package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.settings;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.settings.ItemStackRestrictionEntrySettings;
import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("DataFlowIssue")
@NullMarked
public final class VJEIConfig implements CompilableResource<ItemStackRestrictionEntrySettings.CompilerData, VCompiledJEIConfig> {
    public static final StreamCodec<ByteBuf, VJEIConfig> STREAM_CODEC = ByteBufCodecs.BOOL.map(VJEIConfig::new, VJEIConfig::hideInJEI);
    private boolean hideInJEI;

    public VJEIConfig(boolean hideInJEI) {
        this.hideInJEI = hideInJEI;
    }

    public boolean hideInJEI() {
        return hideInJEI;
    }

    public void hideInJEI(boolean hideInJEI) {
        this.hideInJEI = hideInJEI;
    }

    public VJEIConfig copy() {
        return new VJEIConfig(hideInJEI);
    }

    @Override
    public VCompiledJEIConfig compile(ItemStackRestrictionEntrySettings.CompilerData compilerData) {
        return new VCompiledJEIConfig(hideInJEI);
    }
}
