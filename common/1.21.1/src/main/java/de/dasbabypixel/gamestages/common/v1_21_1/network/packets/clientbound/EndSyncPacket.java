package de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.addon.Addon.ClientRecompilePostEvent;
import de.dasbabypixel.gamestages.common.addon.Addon.ClientRecompilePreEvent;
import de.dasbabypixel.gamestages.common.addon.Addon.ClientReplaceManagerEvent;
import de.dasbabypixel.gamestages.common.addon.Addon.ReloadPostEvent;
import de.dasbabypixel.gamestages.common.data.DuplicatesException;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ClientMutableGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static de.dasbabypixel.gamestages.common.addon.Addon.CLIENT_RECOMPILE_POST_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.CLIENT_RECOMPILE_PRE_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.CLIENT_REPLACE_MANAGER_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.RELOAD_POST_EVENT;

@NullMarked
public record EndSyncPacket() implements GameStagesPacket {
    public static final Type<EndSyncPacket> TYPE = new CustomPacketPayload.Type<>(CommonVGameStageMod.location("end_sync"));
    public static final StreamCodec<ByteBuf, EndSyncPacket> STREAM_CODEC = StreamCodec.of((o, endSyncPacket) -> {
    }, byteBuf -> new EndSyncPacket());
    private static final Logger LOGGER = LoggerFactory.getLogger(EndSyncPacket.class);

    @Override
    public void handle() {
        var buildingManager = ClientMutableGameStageManager.buildingInstance();
        RELOAD_POST_EVENT.call(new ReloadPostEvent(buildingManager));
        var compiledManager = buildingManager.finishBuildingInstance();
        var oldManager = ClientGameStageManager.initialized() ? ClientGameStageManager.currentManager() : null;
        ClientGameStageManager.update(compiledManager);
        var player = Objects.requireNonNull(CommonInstances.platformPlayerProvider.clientSelfPlayer());
        var stages = player.getGameStages();
        CLIENT_RECOMPILE_PRE_EVENT.call(new ClientRecompilePreEvent(compiledManager, stages));
        try {
            player.getGameStages().recompileAll(compiledManager);
        } catch (DuplicatesException d) {
            var p = (LocalPlayer) player;
            d.print(s -> {
                LOGGER.error(s);
                p.sendSystemMessage(Component.literal(s).withStyle(ChatFormatting.RED));
            });
        }
        CLIENT_RECOMPILE_POST_EVENT.call(new ClientRecompilePostEvent(compiledManager, stages));
        CLIENT_REPLACE_MANAGER_EVENT.call(new ClientReplaceManagerEvent(oldManager, compiledManager));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
