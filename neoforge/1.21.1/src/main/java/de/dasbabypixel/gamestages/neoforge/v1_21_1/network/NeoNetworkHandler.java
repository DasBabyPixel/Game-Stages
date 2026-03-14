package de.dasbabypixel.gamestages.neoforge.v1_21_1.network;

import de.dasbabypixel.gamestages.common.v1_21_1.addon.PacketRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.StatusPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.SyncRegisteredGameStagesPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.SyncUnlockedGameStagesPacket;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.NeoForgeEntrypoint;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager;
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

        var registry = new PacketRegistryImpl(registrar);
        for (var addon : NeoAddonManager.instance().addons()) {
            addon.registerPackets(registry);
        }

        registry.playClientBound(SyncRegisteredGameStagesPacket.TYPE, SyncRegisteredGameStagesPacket.STREAM_CODEC);
        registry.playClientBound(SyncUnlockedGameStagesPacket.TYPE, SyncUnlockedGameStagesPacket.STREAM_CODEC);
        registry.playClientBound(StatusPacket.TYPE, StatusPacket.STREAM_CODEC);
    }

    private record PacketRegistryImpl(PayloadRegistrar registrar) implements PacketRegistry {
        private static <T extends GameStagesPacket> void handle(T packet, IPayloadContext context) {
            context.enqueueWork(packet::handle).exceptionally(t -> {
                NeoForgeEntrypoint.LOGGER.error("Failed to handle packet", t);
                return null;
            });
        }

        @Override
        public <T extends GameStagesPacket> void playClientBound(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
            registrar.playToClient(type, codec, PacketRegistryImpl::handle);
        }
    }
}
