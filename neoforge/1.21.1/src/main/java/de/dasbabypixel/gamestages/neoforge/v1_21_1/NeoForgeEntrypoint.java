package de.dasbabypixel.gamestages.neoforge.v1_21_1;

import de.dasbabypixel.gamestages.common.BuildConstants;
import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.listener.PlayerJoinListener;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs.PreparedRestrictionPredicateSerializer;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs.RestrictionPredicateSerializer;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollectionSerializer;
import de.dasbabypixel.gamestages.neoforge.NeoForgeInstances;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.Attachments;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.PlatformPlayerStagesProviderImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.entity.PlatformPlayerProviderImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.NeoModProvider;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.network.NeoNetworkHandler;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.network.NeoPlatformPacketHandler;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.network.PlatformPacketDistributorImpl;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(BuildConstants.MOD_ID)
public class NeoForgeEntrypoint {
    public static final Logger LOGGER = LoggerFactory.getLogger(NeoForgeEntrypoint.class);
    public static final Registry<CommonItemCollectionSerializer<?>> ITEM_COLLECTION_SERIALIZER_REGISTRY = new RegistryBuilder<>(CommonItemCollection.REGISTRY_KEY)
            .sync(true)
            .create();
    public static final Registry<RestrictionPredicateSerializer<?>> RESTRICTION_PREDICATE_SERIALIZER_REGISTRY = new RegistryBuilder<>(CommonCodecs.RESTRICTION_PREDICATE_SERIALIZER_REGISTRY_KEY)
            .sync(true)
            .create();
    public static final Registry<PreparedRestrictionPredicateSerializer<?>> PREPARED_RESTRICTION_PREDICATE_SERIALIZER_REGISTRY = new RegistryBuilder<>(CommonCodecs.PREPARED_RESTRICTION_PREDICATE_SERIALIZER_REGISTRY_KEY)
            .sync(true)
            .create();

    static {
        CommonInstances.platformPacketDistributor = new PlatformPacketDistributorImpl();
        CommonInstances.platformPlayerStagesProvider = new PlatformPlayerStagesProviderImpl();
        CommonInstances.platformPlayerProvider = new PlatformPlayerProviderImpl();

        NeoForgeInstances.modProvider = new NeoModProvider();

        CommonVGameStageMod.platformPacketHandler = new NeoPlatformPacketHandler();

        CommonVGameStageMod.init();
    }

    public NeoForgeEntrypoint(IEventBus modBus) {
        modBus.addListener(NeoNetworkHandler::register);
        modBus.addListener(this::handleRegistries);
        modBus.addListener(this::handleRegister);

        NeoForge.EVENT_BUS.addListener(this::handleServerAboutToStart);
        NeoForge.EVENT_BUS.addListener(this::handleServerStopped);
        NeoForge.EVENT_BUS.addListener(this::handlePlayerJoin);

        Attachments.ATTACHMENT_TYPES.register(modBus);
    }

    private void handleRegistries(NewRegistryEvent event) {
        event.register(ITEM_COLLECTION_SERIALIZER_REGISTRY);
        event.register(RESTRICTION_PREDICATE_SERIALIZER_REGISTRY);
        event.register(PREPARED_RESTRICTION_PREDICATE_SERIALIZER_REGISTRY);
    }

    private void handleRegister(RegisterEvent event) {
        event.register(CommonItemCollection.REGISTRY_KEY, registry -> {
            registry.register(CommonVGameStageMod.location("direct"), CommonItemCollectionSerializer.DIRECT);
            registry.register(CommonVGameStageMod.location("except"), CommonItemCollectionSerializer.EXCEPT);
            registry.register(CommonVGameStageMod.location("union"), CommonItemCollectionSerializer.UNION);
        });
        event.register(CommonCodecs.RESTRICTION_PREDICATE_SERIALIZER_REGISTRY_KEY, registry -> {
            registry.register(CommonVGameStageMod.location("game_stage"), RestrictionPredicateSerializer.GAME_STAGE);
            registry.register(CommonVGameStageMod.location("and"), RestrictionPredicateSerializer.AND);
            registry.register(CommonVGameStageMod.location("or"), RestrictionPredicateSerializer.OR);
        });
        event.register(CommonCodecs.PREPARED_RESTRICTION_PREDICATE_SERIALIZER_REGISTRY_KEY, registry -> {
            registry.register(CommonVGameStageMod.location("composite"), PreparedRestrictionPredicateSerializer.COMPOSITE);
            registry.register(CommonVGameStageMod.location("game_stage"), PreparedRestrictionPredicateSerializer.GAME_STAGE);
        });
    }

    private void handlePlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        var player = event.getEntity();
        PlayerJoinListener.handleJoin(player);
    }

    private void handleServerAboutToStart(ServerAboutToStartEvent event) {
        ServerGameStageManager.init();
    }

    private void handleServerStopped(ServerStoppedEvent event) {
        ServerGameStageManager.stop();
    }
}
