package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.settings;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.settings.ItemStackRestrictionEntrySettings;
import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("DataFlowIssue")
@NullMarked
public final class VHiddenName implements CompilableResource<ItemStackRestrictionEntrySettings.CompilerData, VCompiledHiddenName> {
    public static final StreamCodec<ByteBuf, VHiddenName> STREAM_CODEC = StreamCodec.composite(FunctionReference.STREAM_CODEC, VHiddenName::function, ByteBufCodecs.BOOL, VHiddenName::hiddenName, VHiddenName::new);
    private static final Map<FunctionReference, Function> FUNCTIONS = new HashMap<>();
    private FunctionReference function;
    private boolean hiddenName;

    static {
        registerFunction(new FunctionReference("builtin:default"), data -> Component.literal("You haven't unlocked this item yet")
                .withStyle(ChatFormatting.RED));
    }

    public VHiddenName(FunctionReference function, boolean hiddenName) {
        this.function = function;
        this.hiddenName = hiddenName;
    }

    public FunctionReference function() {
        return function;
    }

    public boolean hiddenName() {
        return hiddenName;
    }

    public void setHiddenName(boolean hiddenName) {
        this.hiddenName = hiddenName;
    }

    public void setFunction(String function) {
        this.function = new FunctionReference(function);
    }

    public VHiddenName copy() {
        return new VHiddenName(function, hiddenName);
    }

    @Override
    public VCompiledHiddenName compile(ItemStackRestrictionEntrySettings.CompilerData compilerData) {
        return new VCompiledHiddenName(hiddenName, function.compile(compilerData));
    }

    public static void registerFunction(FunctionReference reference, Function function) {
        FUNCTIONS.put(reference, function);
    }

    @NullMarked
    public interface Function {
        /**
         * Get the hidden name. Return null to use the default display name for an item
         */
        @Nullable MutableComponent getHiddenName(FunctionData data);
    }

    @NullMarked
    public record FunctionData(Player player, CompiledItemStackRestrictionEntry entry, ItemStack itemStack) {
    }

    @NullMarked
    public record FunctionReference(
            String reference) implements CompilableResource<ItemStackRestrictionEntrySettings.CompilerData, Function> {

        public static final StreamCodec<ByteBuf, FunctionReference> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, FunctionReference::reference, FunctionReference::new);

        @Override
        public Function compile(ItemStackRestrictionEntrySettings.CompilerData compilerData) {
            return Objects.requireNonNull(FUNCTIONS.get(this), () -> "Unable to resolve ItemStack HiddenName function for \"" + this.reference + "\"");
        }
    }
}
