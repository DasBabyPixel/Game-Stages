package de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound;

import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.client.network.ClientNetworkHandlers;
import de.dasbabypixel.gamestages.common.data.DuplicatesException;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryPreCompiler;
import de.dasbabypixel.gamestages.common.entity.ClientPlayer;
import de.dasbabypixel.gamestages.common.network.Status;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.ByIdMap;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.function.IntFunction;

@NullMarked
public record StatusPacket(Status status) implements GameStagesPacket {
    public static final Type<StatusPacket> TYPE = new CustomPacketPayload.Type<>(CommonVGameStageMod.location("status"));
    @SuppressWarnings("DataFlowIssue")
    public static final IntFunction<Status> STATUS_BY_ID = ByIdMap.continuous(Enum::ordinal, Status.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, Status> STATUS_STREAM_CODEC = ByteBufCodecs.idMapper(STATUS_BY_ID, Enum::ordinal);
    @SuppressWarnings("DataFlowIssue")
    public static final StreamCodec<ByteBuf, StatusPacket> STREAM_CODEC = StreamCodec.composite(STATUS_STREAM_CODEC, StatusPacket::status, StatusPacket::new);

    @Override
    public void handle() {
        ClientNetworkHandlers.status(status);
        switch (status) {
            case END_SYNC -> {
                var instance = ClientGameStageManager.instance();
                var preCompiler = instance.get(RestrictionEntryPreCompiler.ATTRIBUTE);
                for (var restriction : instance.restrictions()) {
                    preCompiler.precompile(restriction);
                }
                var player = (ClientPlayer) Objects.requireNonNull(Minecraft.getInstance().player);
                try {
                    player.getGameStages().recompileAll(instance);
                } catch (DuplicatesException d) {
                    var p = (LocalPlayer) player;
                    d.print(s -> {
                        System.err.println(s);
                        p.sendSystemMessage(Component.literal(s).withStyle(ChatFormatting.RED));
                    });
                }
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
