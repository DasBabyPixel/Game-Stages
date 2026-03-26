package de.dasbabypixel.gamestages.neoforge.v1_21_1;

import de.dasbabypixel.gamestages.common.BuildConstants;
import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.addon.ContentRegistry;
import de.dasbabypixel.gamestages.common.addon.ContentRegistryImpl;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import de.dasbabypixel.gamestages.common.listener.PlayerJoinListener;
import de.dasbabypixel.gamestages.common.listener.PlayerQuitListener;
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
import de.dasbabypixel.gamestages.neoforge.v1_21_1.entity.IBlockEntity;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.entity.PlatformPlayerProviderImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.NeoModProvider;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.RegisterEventJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs.StagesProbeJSPlugin;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.network.NeoNetworkHandler;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.network.PlatformPacketDistributorImpl;
import dev.ftb.mods.ftbteams.api.event.PlayerJoinedPartyTeamEvent;
import dev.ftb.mods.ftbteams.api.event.PlayerLeftPartyTeamEvent;
import dev.ftb.mods.ftbteams.api.event.TeamEvent;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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
        NeoForge.EVENT_BUS.addListener(this::handlePlayerQuit);
        NeoForge.EVENT_BUS.addListener(this::handleChunkLoad);
        NeoForge.EVENT_BUS.addListener(this::handleChunkUnload);
        NeoForge.EVENT_BUS.addListener(this::handleBlockPlace);

        ReloadHandler.registerListeners();

        if (Mods.FTB_TEAMS.isLoaded()) {
            TeamEvent.DELETED.register(this::onTeamDelete);
            TeamEvent.PLAYER_JOINED_PARTY.register(this::onTeamJoin);
            TeamEvent.PLAYER_LEFT_PARTY.register(this::onTeamLeave);
        }
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

    private void onTeamJoin(PlayerJoinedPartyTeamEvent event) {
        System.out.println("join team " + event.getTeam().getId());
        event.getPlayer().getGameStages().setTeam(event.getTeam().getId());
    }

    private void onTeamLeave(PlayerLeftPartyTeamEvent event) {
        System.out.println("leave team " + event.getTeam().getId());
        if (event.getPlayer() == null) {
            var cache = Objects.requireNonNull(ServerGameStageManager.INSTANCE).playerStagesCache();
            var stages = cache.requirePlayer(event.getPlayerId());
            stages.setTeam(null);
            cache.release(stages);
        } else {
            event.getPlayer().getGameStages().setTeam(null);
        }
    }

    private void onTeamDelete(TeamEvent event) {
        var id = event.getTeam().getId();
        System.out.println("Delete team " + id);
    }

    private void handleChunkLoad(ChunkEvent.Load event) {
        var chunk = event.getChunk();
        var level = chunk.getLevel();
        if (level == null) return;
        if (level.getServer() == null) {
        }
        // TODO
    }

    private void handleChunkUnload(ChunkEvent.Unload event) {
        var chunk = event.getChunk();
        var level = chunk.getLevel();
        if (level == null) return;
        if (level.getServer() == null) {
        }
        // TODO
    }

    private void handleBlockPlace(BlockEvent.EntityPlaceEvent event) {
        var entity = event.getEntity();
        if (entity == null) return;
        var source = entity.getData(Attachments.SOURCE);
        var owners = source.owners();
        if (owners.isEmpty()) return;
        var level = event.getLevel();
        if (level.getServer() == null) return;

        if (event instanceof BlockEvent.EntityMultiPlaceEvent e) {
            for (var s : e.getReplacedBlockSnapshots()) {
                var blockEntity = level.getBlockEntity(s.getPos());
                if (blockEntity != null) handleBlockPlaceInternal(blockEntity, owners);
            }
        } else {
            var blockEntity = level.getBlockEntity(event.getPos());
            if (blockEntity != null) handleBlockPlaceInternal(blockEntity, owners);
        }
    }

    private void handleBlockBreak(BlockEvent.BreakEvent event) {
    }

    private void handleBlockPlaceInternal(BlockEntity blockEntity, Set<UUID> owners) {
        System.out.println("Attach origin to " + blockEntity.getBlockPos());
        var data = blockEntity.getData(Attachments.SOURCE);
        data.setOwners(owners);
        blockEntity.setChanged();
        ((IBlockEntity) blockEntity).reloadOwners();
    }

    private void handlePlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        var player = event.getEntity();
        PlayerJoinListener.handleJoin(player);
    }

    private void handlePlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        var player = event.getEntity();
        PlayerQuitListener.handleQuit((ServerPlayer) player);
    }

    private void handleServerAboutToStart(ServerAboutToStartEvent event) {
        var dataDirectory = event.getServer().storageSource.getLevelDirectory().path().resolve("gamestages");
        ServerGameStageManager.init(dataDirectory);
    }

    private void handleServerStopped(ServerStoppedEvent event) {
        ServerGameStageManager.stop();
    }
}
