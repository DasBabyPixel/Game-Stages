package de.dasbabypixel.gamestages.neoforge.v1_21_1.network;

import de.dasbabypixel.gamestages.common.entity.Player;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.network.PlatformPacketDistributor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

public class PlatformPacketDistributorImpl implements PlatformPacketDistributor {
    @Override
    public void sendToServer(CustomPacket packet) {
        Objects.requireNonNull(Minecraft.getInstance().getConnection()).send((CustomPacketPayload) packet);
    }

    @Override
    public void sendToPlayer(Player player, CustomPacket packet) {
        ((ServerPlayer) player).connection.send((CustomPacketPayload) packet);
    }

    @Override
    public void sendToAllPlayers(CustomPacket packet) {
        PacketDistributor.sendToAllPlayers((CustomPacketPayload) packet);
    }
}
