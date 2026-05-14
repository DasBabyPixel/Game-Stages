package de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ClientMutableGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NullMarked;

import static de.dasbabypixel.gamestages.common.addon.Addon.RELOAD_PRE_EVENT;

@NullMarked
public record BeginSyncPacket(int version) implements GameStagesPacket {
    public static final CustomPacketPayload.Type<BeginSyncPacket> TYPE = new CustomPacketPayload.Type<>(CommonVGameStageMod.location("begin_sync"));
    @SuppressWarnings("DataFlowIssue")
    public static final StreamCodec<ByteBuf, BeginSyncPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, BeginSyncPacket::version, BeginSyncPacket::new);

    @Override
    public void handle() {
        var manager = ClientMutableGameStageManager.beginBuildingInstance();
        RELOAD_PRE_EVENT.call(new Addon.ReloadPreEvent(manager));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
