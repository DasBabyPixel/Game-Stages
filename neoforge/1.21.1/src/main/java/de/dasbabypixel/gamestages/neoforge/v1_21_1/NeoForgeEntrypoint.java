package de.dasbabypixel.gamestages.neoforge.v1_21_1;

import de.dasbabypixel.gamestages.common.BuildConstants;
import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.addon.ContentRegistry;
import de.dasbabypixel.gamestages.common.addon.ContentRegistryImpl;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.listener.PlayerJoinListener;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VContentRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs.PreparedRestrictionPredicateSerializer;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs.RestrictionPredicateSerializer;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContent;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContentSerializer;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContentType;
import de.dasbabypixel.gamestages.common.v1_21_1.data.flattener.CommonGameContentFlattener;
import de.dasbabypixel.gamestages.neoforge.NeoForgeInstances;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.EventRegistryImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.commands.StageArgumentType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.commands.StagesCommand;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.Attachments;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.PlatformPlayerStagesProviderImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.entity.PlatformPlayerProviderImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.NeoModProvider;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.RegisterEventJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs.StagesProbeJSPlugin;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.network.NeoNetworkHandler;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.network.PlatformPacketDistributorImpl;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod.location;

@Mod(BuildConstants.MOD_ID)
public class NeoForgeEntrypoint {
    public static final Logger LOGGER = LoggerFactory.getLogger(NeoForgeEntrypoint.class);
    public static final Registry<CommonGameContentType<?>> GAME_CONTENT_TYPE_SERIALIZER_REGISTRY = new RegistryBuilder<>(CommonGameContentType.REGISTRY_KEY)
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

        CommonVGameStageMod.init();
    }

    private boolean addonsFrozen = false;
    private @Nullable ContentRegistryImpl contentRegistry;

    public NeoForgeEntrypoint(IEventBus modBus) {
        modBus.addListener(NeoNetworkHandler::register);
        modBus.addListener(this::handleRegistries);
        modBus.addListener(this::handleRegister);
        modBus.addListener(this::handleCommonSetup);
        modBus.addListener(this::handleInterModProcess);
        modBus.addListener(this::handleLoadComplete);
        Attachments.ATTACHMENT_TYPES.register(modBus);

        NeoForge.EVENT_BUS.addListener(this::handleRegisterCommands);
        NeoForge.EVENT_BUS.addListener(this::handleServerAboutToStart);
        NeoForge.EVENT_BUS.addListener(this::handleServerStopped);
        NeoForge.EVENT_BUS.addListener(this::handlePlayerJoin);

        ReloadHandler.registerListeners();
    }

    private synchronized void loadAndFreezeAddons() {
        if (addonsFrozen) return;
        addonsFrozen = true;

        NeoAddonManager.init();
        InterModComms.getMessages(BuildConstants.MOD_ID, s -> s.equals("register_addon")).forEach(msg -> {
            var r = (NeoAddonManager.Registration) msg.messageSupplier().get();
            NeoAddonManager.instance().addAddon(r.id(), r.addon());
        });
        NeoAddonManager.done();
        var m = NeoAddonManager.instance();
        for (var addon : m.addons()) {
            addon.onRegister(m);
        }
    }

    private synchronized ContentRegistryImpl contentRegistry() {
        if (contentRegistry == null) {
            loadAndFreezeAddons();
            contentRegistry = new ContentRegistryImpl();

            for (var addon : NeoAddonManager.instance().addons()) {
                addon.registerCustomContent(contentRegistry);
            }
        }
        return contentRegistry;
    }

    private void processMessages() {
    }

    private void handleInterModProcess(InterModProcessEvent event) {
        processMessages();
    }

    private void handleLoadComplete(FMLLoadCompleteEvent event) {
        if (Mods.KUBEJS.isLoaded()) {
            var eventRegistry = new EventRegistryImpl();
            eventRegistry.add(RegisterEventJS.class, RegisterEventJS.TYPE);
            for (var addon : NeoAddonManager.instance().addons()) {
                addon.createKubeJSSupport().registerEventExtensions(eventRegistry);
            }
            eventRegistry.freeze();

            if (Mods.PROBEJS.isLoaded()) {
                StagesProbeJSPlugin.eventRegistry = eventRegistry;
            }
        }
    }

    private void handleCommonSetup(FMLCommonSetupEvent event) {
        for (var entry : contentRegistry().entries()) {
            CommonGameContentFlattener.addFlattener(entry.attribute(ContentRegistry.FLATTENER_FACTORY));
        }
    }

    private void handleRegistries(NewRegistryEvent event) {
        event.register(RESTRICTION_PREDICATE_SERIALIZER_REGISTRY);
        event.register(PREPARED_RESTRICTION_PREDICATE_SERIALIZER_REGISTRY);
        event.register(GAME_CONTENT_TYPE_SERIALIZER_REGISTRY);
    }

    private void handleRegister(RegisterEvent event) {
        event.register(CommonGameContent.REGISTRY_KEY, registry -> {
            registry.register(location("mod"), CommonGameContentSerializer.MOD);
            registry.register(location("filter_type"), CommonGameContentSerializer.FILTER_TYPE);
            registry.register(location("except"), CommonGameContentSerializer.EXCEPT);
            registry.register(location("only"), CommonGameContentSerializer.ONLY);
            registry.register(location("union"), CommonGameContentSerializer.UNION);

            for (var entry : contentRegistry().entries()) {
                registry.register(location(entry.attribute(ContentRegistry.NAME) + "_collection"), entry.attribute(VContentRegistry.GAME_CONTENT_SERIALIZER));
            }

        });
        event.register(CommonCodecs.RESTRICTION_PREDICATE_SERIALIZER_REGISTRY_KEY, registry -> {
            registry.register(location("game_stage"), RestrictionPredicateSerializer.GAME_STAGE);
            registry.register(location("and"), RestrictionPredicateSerializer.AND);
            registry.register(location("or"), RestrictionPredicateSerializer.OR);
        });
        event.register(CommonCodecs.PREPARED_RESTRICTION_PREDICATE_SERIALIZER_REGISTRY_KEY, registry -> {
            registry.register(location("composite"), PreparedRestrictionPredicateSerializer.COMPOSITE);
            registry.register(location("game_stage"), PreparedRestrictionPredicateSerializer.GAME_STAGE);
        });
        event.register(CommonGameContentType.REGISTRY_KEY, registry -> {
            for (var entry : contentRegistry().entries()) {
                registry.register(location(entry.attribute(ContentRegistry.NAME)), (CommonGameContentType<?>) entry.type());
            }
        });
        event.register(Registries.COMMAND_ARGUMENT_TYPE, registry -> registry.register(location("stage"), ArgumentTypeInfos.registerByClass(StageArgumentType.class, new StageArgumentType.Info())));
    }

    private void handleRegisterCommands(RegisterCommandsEvent event) {
        StagesCommand.register(event.getDispatcher());
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
