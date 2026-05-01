package de.dasbabypixel.gamestages.neoforge.v1_21_1;

import de.dasbabypixel.gamestages.common.BuildConstants;
import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.addon.Addon.RegisterCustomContentEvent;
import de.dasbabypixel.gamestages.common.addon.ContentRegistry;
import de.dasbabypixel.gamestages.common.addon.ContentRegistryImpl;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenResolverFactory;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.server.GlobalServerState;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import de.dasbabypixel.gamestages.common.listener.PlayerJoinListener;
import de.dasbabypixel.gamestages.common.listener.PlayerQuitListener;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VContentRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenTypes;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs.PreparedRestrictionPredicateSerializer;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs.RestrictionPredicateSerializer;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContent;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContentSerializer;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContentType;
import de.dasbabypixel.gamestages.common.v1_21_1.data.flattener.CommonGameContentFlattener;
import de.dasbabypixel.gamestages.common.v1_21_1.data.graph.IngredientContent;
import de.dasbabypixel.gamestages.neoforge.NeoForgeInstances;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.EventRegistryImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.datadriven.NeoDataDrivenTypes;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeJEI;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.client.ClientReloadHandler;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.commands.StageArgumentType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.commands.StagesCommand;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.Attachments;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static de.dasbabypixel.gamestages.common.addon.Addon.REGISTER_CUSTOM_CONTENT_EVENT;
import static de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod.location;

@Mod(BuildConstants.MOD_ID)
@NullMarked
public class NeoForgeEntrypoint {
    public static final Logger LOGGER = LoggerFactory.getLogger(NeoForgeEntrypoint.class);
    public static final Registry<CommonGameContentType<?>> GAME_CONTENT_TYPE_SERIALIZER_REGISTRY = new RegistryBuilder<>(CommonGameContentType.REGISTRY_KEY).sync(true)
            .create();
    public static final Registry<RestrictionPredicateSerializer<?>> RESTRICTION_PREDICATE_SERIALIZER_REGISTRY = new RegistryBuilder<>(CommonCodecs.RESTRICTION_PREDICATE_SERIALIZER_REGISTRY_KEY).sync(true)
            .create();
    public static final Registry<PreparedRestrictionPredicateSerializer<?>> PREPARED_RESTRICTION_PREDICATE_SERIALIZER_REGISTRY = new RegistryBuilder<>(CommonCodecs.PREPARED_RESTRICTION_PREDICATE_SERIALIZER_REGISTRY_KEY).sync(true)
            .create();
    private boolean addonsFrozen = false;
    private @Nullable ContentRegistryImpl contentRegistry;

    static {
        CommonInstances.platformPacketDistributor = new PlatformPacketDistributorImpl();
        CommonInstances.platformPlayerProvider = new PlatformPlayerProviderImpl();

        NeoForgeInstances.modProvider = new NeoModProvider();

        CommonVGameStageMod.init();
    }

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
        if (FMLEnvironment.dist.isClient()) {
            NeoForge.EVENT_BUS.addListener(this::handleRecipes);
            ClientReloadHandler.registerListeners();
        }

        ReloadHandler.registerListeners();

        ItemStackRestrictionResolverFactories.instance().register(new DataDrivenResolverFactory());

        IngredientContent.platformIngredientHelper = new IngredientContent.PlatformIngredientHelper() {
            @Override
            public String toString(Ingredient ingredient) {
                if (ingredient.isCustom()) {
                    var c = Objects.requireNonNull(ingredient.getCustomIngredient());
                    return Objects.requireNonNull(NeoForgeRegistries.INGREDIENT_TYPES)
                            .getKey(c.getType()) + "[" + c.getItems().toList() + "]";
                } else {
                    return Arrays.toString(ingredient.getValues());
                }
            }
        };

        if (Mods.FTB_TEAMS.isLoaded()) {
            Objects.requireNonNull(TeamEvent.DELETED).register(this::onTeamDelete);
            Objects.requireNonNull(TeamEvent.PLAYER_JOINED_PARTY).register(this::onTeamJoin);
            Objects.requireNonNull(TeamEvent.PLAYER_LEFT_PARTY).register(this::onTeamLeave);
        }
    }

    private synchronized void loadAndFreezeAddons() {
        if (addonsFrozen) return;
        addonsFrozen = true;

        NeoAddonManager.init();
        InterModComms.getMessages(BuildConstants.MOD_ID, "register_addon"::equals).forEach(msg -> {
            var r = (NeoAddonManager.Registration) Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(msg)
                    .messageSupplier()).get());
            NeoAddonManager.instance().addAddon(Objects.requireNonNull(r.id()), Objects.requireNonNull(r.addon()));
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

            REGISTER_CUSTOM_CONTENT_EVENT.call(new RegisterCustomContentEvent(contentRegistry));

            for (var entry : contentRegistry.entries()) {
                GameContentType.TYPES.add(entry.type());
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
        NeoDataDrivenTypes.register(DataDrivenTypes.instance(), ItemStackRestrictionResolverFactories.instance());

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
            assert registry != null;
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
            assert registry != null;
            registry.register(location("game_stage"), RestrictionPredicateSerializer.GAME_STAGE);
            registry.register(location("and"), RestrictionPredicateSerializer.AND);
            registry.register(location("or"), RestrictionPredicateSerializer.OR);
            registry.register(location("true"), RestrictionPredicateSerializer.TRUE);
            registry.register(location("false"), RestrictionPredicateSerializer.FALSE);
            registry.register(location("not"), RestrictionPredicateSerializer.NOT);
        });
        event.register(CommonCodecs.PREPARED_RESTRICTION_PREDICATE_SERIALIZER_REGISTRY_KEY, registry -> {
            assert registry != null;
            registry.register(location("composite"), PreparedRestrictionPredicateSerializer.COMPOSITE);
            registry.register(location("game_stage"), PreparedRestrictionPredicateSerializer.GAME_STAGE);
        });
        event.register(CommonGameContentType.REGISTRY_KEY, registry -> {
            assert registry != null;
            for (var entry : contentRegistry().entries()) {
                registry.register(location(entry.attribute(ContentRegistry.NAME)), (CommonGameContentType<?>) entry.type());
            }
        });
        event.register(Registries.COMMAND_ARGUMENT_TYPE, registry -> {
            assert registry != null;
            registry.register(location("stage"), ArgumentTypeInfos.registerByClass(StageArgumentType.class, new StageArgumentType.Info()));
        });
    }

    private void handleRecipes(RecipesUpdatedEvent event) {
        RecipeJEI.recipeManager = event.getRecipeManager();
        System.out.println("Received recipes on client");
        System.out.println("Received recipes on client");
    }

    private void handleRegisterCommands(RegisterCommandsEvent event) {
        StagesCommand.register(event.getDispatcher());
    }

    private void onTeamJoin(@Nullable PlayerJoinedPartyTeamEvent event) {
        Objects.requireNonNull(event);
        var team = Objects.requireNonNull(event.getTeam());
        event.getPlayer().getGameStages().setTeam(team.getId());
    }

    private void onTeamLeave(@Nullable PlayerLeftPartyTeamEvent event) {
        Objects.requireNonNull(event);
        var team = Objects.requireNonNull(event.getTeam());
        if (event.getPlayer() == null) {
            var cache = GlobalServerState.state().stagesCache();
            var stages = cache.requirePlayer(Objects.requireNonNull(event.getPlayerId()));
            stages.setTeam(null);
            cache.release(stages);
        } else {
            event.getPlayer().getGameStages().setTeam(null);
        }
    }

    private void onTeamDelete(@Nullable TeamEvent event) {
        Objects.requireNonNull(event);
        var team = Objects.requireNonNull(event.getTeam());
        var id = team.getId();
    }

    private void handleChunkLoad(ChunkEvent.Load event) {
        Objects.requireNonNull(event);
        var chunk = event.getChunk();
        var level = chunk.getLevel();
        // TODO
    }

    private void handleChunkUnload(ChunkEvent.Unload event) {
        Objects.requireNonNull(event);
        var chunk = event.getChunk();
        var level = chunk.getLevel();
        // TODO
    }

    private void handleBlockPlace(BlockEvent.EntityPlaceEvent event) {
        Objects.requireNonNull(event);
        var entity = event.getEntity();
        if (entity == null) return;
        var source = entity.getData(Attachments.SOURCE);
        var owners = source.owners();
        if (owners.isEmpty()) return;
        var level = event.getLevel();
        if (level.getServer() == null) return;

        if (event instanceof BlockEvent.EntityMultiPlaceEvent e) {
            for (var s : e.getReplacedBlockSnapshots()) {
                assert s != null;
                var blockEntity = level.getBlockEntity(s.getPos());
                if (blockEntity != null) handleBlockPlaceInternal(blockEntity, owners);
            }
        } else {
            var blockEntity = level.getBlockEntity(event.getPos());
            if (blockEntity != null) handleBlockPlaceInternal(blockEntity, owners);
        }
    }

    private void handleBlockPlaceInternal(BlockEntity blockEntity, Set<UUID> owners) {
        var data = blockEntity.getData(Attachments.SOURCE);
        data.setOwners(owners);
        blockEntity.setChanged();
        blockEntity.reloadOwners();
    }

    private void handlePlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Objects.requireNonNull(event);
        var player = event.getEntity();
        PlayerJoinListener.handleJoin(player);
    }

    private void handlePlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        Objects.requireNonNull(event);
        var player = event.getEntity();
        PlayerQuitListener.handleQuit((ServerPlayer) player);
    }

    private void handleServerAboutToStart(ServerAboutToStartEvent event) {
        Objects.requireNonNull(event);
        var dataDirectory = Objects.requireNonNull(event.getServer().storageSource.getLevelDirectory()
                .path()
                .resolve("gamestages"));
        GlobalServerState.init(dataDirectory);
        ReloadHandler.pushFullUpdate(GlobalServerState.currentManager());
    }

    private void handleServerStopped(ServerStoppedEvent event) {
        Objects.requireNonNull(event);
        GlobalServerState.stop();
    }
}
