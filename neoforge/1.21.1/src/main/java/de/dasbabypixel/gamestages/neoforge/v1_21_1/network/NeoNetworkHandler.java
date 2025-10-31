package de.dasbabypixel.gamestages.neoforge.v1_21_1.network;

import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.CommonItemRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.StatusPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.SyncRegisteredGameStagesPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.SyncUnlockedGameStagesPacket;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.NeoForgeEntrypoint;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NeoNetworkHandler {
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.executesOn(HandlerThread.NETWORK);
        new Register(registrar).run();
    }

    private record Register(PayloadRegistrar registrar) implements Runnable {
        private static <T extends GameStagesPacket> void handle(T packet, IPayloadContext context) {
            context.enqueueWork(packet::handle).exceptionally(t -> {
                NeoForgeEntrypoint.LOGGER.error("Failed to handle packet", t);
                return null;
            });
        }

        private <T extends GameStagesPacket> void playToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
            registrar.playToClient(type, codec, Register::handle);

        }

        @Override
        public void run() {
            playToClient(SyncRegisteredGameStagesPacket.TYPE, SyncRegisteredGameStagesPacket.STREAM_CODEC);
            playToClient(SyncUnlockedGameStagesPacket.TYPE, SyncUnlockedGameStagesPacket.STREAM_CODEC);
            playToClient(StatusPacket.TYPE, StatusPacket.STREAM_CODEC);
            playToClient(CommonItemRestrictionPacket.TYPE, CommonItemRestrictionPacket.STREAM_CODEC);
        }
    }
}
