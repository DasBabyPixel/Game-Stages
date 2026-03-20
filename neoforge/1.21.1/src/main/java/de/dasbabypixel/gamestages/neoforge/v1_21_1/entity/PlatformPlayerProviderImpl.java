package de.dasbabypixel.gamestages.neoforge.v1_21_1.entity;

import de.dasbabypixel.gamestages.common.entity.ClientPlayer;
import de.dasbabypixel.gamestages.common.entity.PlatformPlayerProvider;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class PlatformPlayerProviderImpl implements PlatformPlayerProvider {
    @Override
    public ClientPlayer clientSelfPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public @NonNull Collection<? extends ServerPlayer> allPlayers() {
        return Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer()).getPlayerList().getPlayers();
    }

    @Override
    public @Nullable ServerPlayer getPlayer(UUID uuid) {
        return Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer()).getPlayerList().getPlayer(uuid);
    }
}
