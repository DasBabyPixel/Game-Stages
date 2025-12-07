package de.dasbabypixel.gamestages.common.client;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ClientGameStageManager extends AbstractGameStageManager {
    public static final List<Addon> ADDONS = new ArrayList<>();
    private static ClientGameStageManager INSTANCE;
    private final List<Addon> addons = List.copyOf(ADDONS);

    private ClientGameStageManager() {
    }

    public static ClientGameStageManager instance() {
        if (INSTANCE == null) INSTANCE = new ClientGameStageManager();
        return INSTANCE;
    }

    public @Nullable Player player() {
        return CommonInstances.platformPlayerProvider.clientSelfPlayer();
    }

    @Override
    public @NonNull List<@NonNull Addon> addons() {
        return addons;
    }
}
