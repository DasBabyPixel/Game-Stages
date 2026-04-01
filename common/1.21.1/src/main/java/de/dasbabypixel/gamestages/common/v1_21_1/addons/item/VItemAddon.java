package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addon.AddonManager;
import de.dasbabypixel.gamestages.common.addon.ContentRegistry;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactory;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.PacketRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VContentRegistry;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class VItemAddon implements VAddon {
    private static VItemAddon instance;

    private final ItemRecipeIntegration recipeIntegration = new ItemRecipeIntegration();

    public VItemAddon() {
        instance = this;
    }

    @Override
    public void onRegister(@NonNull AddonManager<? extends Addon> addonManager) {
        recipeIntegration.register(addonManager);
    }

    @Override
    public void preReload(@NonNull AbstractGameStageManager instance) {
        VAddon.super.preReload(instance);
    }

    @Override
    public void postReload(@NonNull AbstractGameStageManager instance) {
        VAddon.super.postReload(instance);
    }

    @Override
    public void preCompileAll(@NonNull RecompilationTask recompilationTask) {
        VAddon.super.preCompileAll(recompilationTask);
        var factoryContextMap = new HashMap<@NonNull ItemStackRestrictionResolverFactory<?>, Object>();
        for (var factory : ItemStackRestrictionResolverFactories.instance().getAll()) {
            factoryContextMap.put(factory, factory.createContext(recompilationTask));
        }
        recompilationTask.setContext(this, new ItemCompileContext(factoryContextMap));
    }

    @Override
    public void postCompileAll(@NonNull RecompilationTask recompilationTask) {
        VAddon.super.postCompileAll(recompilationTask);
        var context = (ItemCompileContext) Objects.requireNonNull(recompilationTask.getContext(this));
    }

    @Override
    public void registerCustomContent(@NonNull ContentRegistry registry) {
        registry
                .prepare(CommonItemCollection.TYPE)
                .set(ContentRegistry.NAME, "item")
                .set(ContentRegistry.FLATTENER_FACTORY, new ItemFlattenerFactory())
                .set(VContentRegistry.GAME_CONTENT_SERIALIZER, CommonItemCollection.SERIALIZER)
                .register();
    }

    @Override
    public void registerPackets(@NonNull PacketRegistry registry) {
        registry.playClientBound(CommonItemRestrictionPacket.TYPE, CommonItemRestrictionPacket.STREAM_CODEC);
    }

    public abstract void handle(@NonNull CommonItemRestrictionPacket packet);

    public static @NonNull VItemAddon instance() {
        return Objects.requireNonNull(instance);
    }

    private record ItemCompileContext(
            @NonNull Map<@NonNull ItemStackRestrictionResolverFactory<?>, Object> factoryContextMap) {
    }
}
