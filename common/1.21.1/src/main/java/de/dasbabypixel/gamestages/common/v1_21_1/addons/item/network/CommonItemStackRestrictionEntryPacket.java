package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.VItemAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record CommonItemStackRestrictionEntryPacket(ItemStackRestrictionEntryReference reference,
                                                    ItemStackRestrictionEntry entry) implements GameStagesPacket {
    public static final Type<CommonItemStackRestrictionEntryPacket> TYPE = new Type<>(CommonVGameStageMod.location("item_stack_restriction_entry"));
    @SuppressWarnings("DataFlowIssue")
    public static final StreamCodec<RegistryFriendlyByteBuf, CommonItemStackRestrictionEntryPacket> STREAM_CODEC = StreamCodec.composite(DataDrivenNetwork.ITEM_STACK_RESTRICTION_ENTRY_REFERENCE_STREAM_CODEC, CommonItemStackRestrictionEntryPacket::reference, DataDrivenNetwork.ITEM_STACK_RESTRICTION_ENTRY_STREAM_CODEC, CommonItemStackRestrictionEntryPacket::entry, CommonItemStackRestrictionEntryPacket::new);

    @Override
    public void handle() {
        VItemAddon.instance().handle(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
