package de.dasbabypixel.gamestages.neoforge.v1_21_1.client;

import de.dasbabypixel.gamestages.common.client.ClientPlayerStages;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ClientReloadHandler {
    public static @Nullable ClientPlayerStages stages;

    public static void registerListeners() {
        NeoForge.EVENT_BUS.addListener(ClientReloadHandler::handleLogin);
        NeoForge.EVENT_BUS.addListener(ClientReloadHandler::handleLogout);
    }

    private static void handleLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        stages = new ClientPlayerStages();
        ClientGameStageManager.activate();
    }

    private static void handleLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        stages = null;
        if (event.getConnection() != null) {
            ClientGameStageManager.deactivate();
        }
    }
}
