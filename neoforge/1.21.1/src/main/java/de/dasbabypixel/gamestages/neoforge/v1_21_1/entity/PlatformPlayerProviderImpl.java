package de.dasbabypixel.gamestages.neoforge.v1_21_1.entity;

import de.dasbabypixel.gamestages.common.entity.ClientPlayer;
import de.dasbabypixel.gamestages.common.entity.PlatformPlayerProvider;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@NullMarked
public class PlatformPlayerProviderImpl implements PlatformPlayerProvider {
    @Override
    public @Nullable ClientPlayer clientSelfPlayer() {
        return Objects.requireNonNull(Minecraft.getInstance()).player;
    }

    @Override
    public Collection<? extends ServerPlayer> allPlayers() {
        var currentServer = ServerLifecycleHooks.getCurrentServer();
        if (currentServer == null) return List.of();
        return currentServer.getPlayerList().getPlayers();
    }

    @Override
    public @Nullable ServerPlayer getPlayer(UUID uuid) {
        return Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer()).getPlayerList().getPlayer(uuid);
    }
}
