package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.settings;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.settings.ItemStackRestrictionEntrySettings;
import de.dasbabypixel.gamestages.common.data.attribute.CompilableAttribute;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ServerMutableGameStageManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("DataFlowIssue")
@NullMarked
public final class VItemStackRestrictionEntrySettings implements ItemStackRestrictionEntrySettings {
    public static final CompilableAttribute<ServerMutableGameStageManager, VItemStackRestrictionEntrySettings, ServerGameStageManager> DEFAULT_SETTINGS_ATTRIBUTE = CompilableAttribute.noop();
    public static final StreamCodec<ByteBuf, VItemStackRestrictionEntrySettings> STREAM_CODEC = StreamCodec.composite(VHiddenName.STREAM_CODEC, VItemStackRestrictionEntrySettings::hiddenName, VItemStackRestrictionEntrySettings::new);
    public static final VItemStackRestrictionEntrySettings DEFAULT_SETTINGS = createFreshDefaults();
    private final VHiddenName hiddenName;

    public VItemStackRestrictionEntrySettings(VHiddenName hiddenName) {
        this.hiddenName = hiddenName;
    }

    @Override
    public VCompiledItemStackRestrictionEntrySettings compile(CompilerData compilerData) {
        return new VCompiledItemStackRestrictionEntrySettings(hiddenName.compile(compilerData));
    }

    public VHiddenName hiddenName() {
        return hiddenName;
    }

    public VItemStackRestrictionEntrySettings copy() {
        return new VItemStackRestrictionEntrySettings(hiddenName.copy());
    }

    private static VItemStackRestrictionEntrySettings createFreshDefaults() {
        var hiddenName = new VHiddenName(new VHiddenName.FunctionReference("builtin:default"), true);
        return new VItemStackRestrictionEntrySettings(hiddenName);
    }

    public static VItemStackRestrictionEntrySettings create(ServerMutableGameStageManager manager) {
        return manager.get(DEFAULT_SETTINGS_ATTRIBUTE).copy();
    }
}
