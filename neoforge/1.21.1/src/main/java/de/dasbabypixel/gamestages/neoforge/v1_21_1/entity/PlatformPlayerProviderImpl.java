package de.dasbabypixel.gamestages.neoforge.v1_21_1.entity;

import de.dasbabypixel.gamestages.common.entity.PlatformPlayerProvider;
import de.dasbabypixel.gamestages.common.entity.Player;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;

public class PlatformPlayerProviderImpl implements PlatformPlayerProvider {
    @Override
    public Player clientSelfPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public @NonNull Collection<? extends Player> allPlayers() {
        return Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer()).getPlayerList().getPlayers();
    }
}
