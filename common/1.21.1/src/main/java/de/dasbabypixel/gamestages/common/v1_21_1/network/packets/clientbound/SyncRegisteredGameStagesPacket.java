package de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound;

import de.dasbabypixel.gamestages.common.client.network.ClientNetworkHandlers;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.util.GameStagePayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncRegisteredGameStagesPacket(List<GameStage> gameStages) implements GameStagesPacket {
    public static final Type<SyncRegisteredGameStagesPacket> TYPE = new CustomPacketPayload.Type<>(CommonVGameStageMod.location("sync_registered_game_stages"));
    public static final StreamCodec<FriendlyByteBuf, SyncRegisteredGameStagesPacket> STREAM_CODEC = GameStagePayload.STREAM_CODEC_LIST.map(SyncRegisteredGameStagesPacket::new, SyncRegisteredGameStagesPacket::gameStages);

    @Override
    public void handle() {
        ClientNetworkHandlers.syncRegisteredGameStages(gameStages);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
