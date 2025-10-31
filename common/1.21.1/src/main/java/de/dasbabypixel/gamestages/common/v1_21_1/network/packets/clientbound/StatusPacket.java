package de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound;

import de.dasbabypixel.gamestages.common.client.network.ClientNetworkHandlers;
import de.dasbabypixel.gamestages.common.network.Status;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.ByIdMap;
import org.jspecify.annotations.NonNull;

import java.util.function.IntFunction;

public record StatusPacket(Status status) implements GameStagesPacket {
    public static final Type<StatusPacket> TYPE = new CustomPacketPayload.Type<>(CommonVGameStageMod.location("status"));
    public static final IntFunction<Status> STATUS_BY_ID = ByIdMap.continuous(Enum::ordinal, Status.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, Status> STATUS_STREAM_CODEC = ByteBufCodecs.idMapper(STATUS_BY_ID, Enum::ordinal);
    public static final StreamCodec<ByteBuf, StatusPacket> STREAM_CODEC = StreamCodec.composite(STATUS_STREAM_CODEC, StatusPacket::status, StatusPacket::new);

    @Override
    public void handle() {
        ClientNetworkHandlers.status(status);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
