package de.dasbabypixel.gamestages.neoforge.v1_21_1;

import de.dasbabypixel.gamestages.common.BuildConstants;
import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.neoforge.NeoForgeInstances;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.Attachments;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.PlatformPlayerStagesProviderImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.NeoModProvider;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.KJSCommonProviderImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.network.NeoNetworkHandler;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.network.PlatformPacketDistributorImpl;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Mod(BuildConstants.MOD_ID)
public class NeoForgeEntrypoint {

    static {
        CommonInstances.kjsCommonProvider = new KJSCommonProviderImpl();
        CommonInstances.platformPacketDistributor = new PlatformPacketDistributorImpl();
        CommonInstances.platformPlayerStagesProvider = new PlatformPlayerStagesProviderImpl();

        NeoForgeInstances.modProvider = new NeoModProvider();

        CommonVGameStageMod.init();
    }

    public NeoForgeEntrypoint(IEventBus modBus) {
        modBus.addListener(NeoNetworkHandler::register);

        NeoForge.EVENT_BUS.addListener(this::handleServerAboutToStart);
        NeoForge.EVENT_BUS.addListener(this::handleServerStopped);

        Attachments.ATTACHMENT_TYPES.register(modBus);
    }

    private void handleServerAboutToStart(ServerAboutToStartEvent event) {
        ServerGameStageManager.init();
    }

    private void handleServerStopped(ServerStoppedEvent event) {
        ServerGameStageManager.stop();
    }
}
