package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionEntry;
import de.dasbabypixel.gamestages.neoforge.integration.Mod;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.ExDeorumJEIIntegration;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.client.ContentVisibilityUpdater;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.config.GameStagesClientConfig;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei.JEIAddon;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class RecipeJEI {
    private static @Nullable RecipeJEI instance;
    private static final Mod EX_DEORUM = Mods.mod("exdeorum");
    private static final Logger LOGGER = Objects.requireNonNull(LoggerFactory.getLogger(RecipeJEI.class));
    private static final RecipeConverter DEFAULT_CONVERTER = RecipeJEI::convert;
    private final Map<net.minecraft.world.item.crafting.RecipeType<?>, RecipeConverter> converterMap = new HashMap<>();
    private @Nullable Context context;
    private final ContentVisibilityUpdater<RecipeAndType<?>, CommonRecipeRestrictionEntry.Compiled> updater = new ContentVisibilityUpdater<>(CommonRecipeCollection.TYPE) {
        @Override
        protected void collect(BaseStages stages, BaseStages.CompileIndex compileIndex, CommonRecipeRestrictionEntry.Compiled compiledEntry, Collector<RecipeAndType<?>> collector) {
            if (!compiledEntry.hideInJEI()) return;
            var converter = new Converter();
            converter.add(compiledEntry.gameContent());
            if (compiledEntry.predicate().test()) {
                collector.showAll(converter.convert());
            } else {
                collector.hideAll(converter.convert());
            }
        }

        @Override
        protected void registerUpdateNotifier(BaseStages stages, BaseStages.CompileIndex compileIndex, List<CommonRecipeRestrictionEntry.Compiled> compiledEntries, UpdateRegistrar<RecipeAndType<?>> registrar) {
            for (var compiledEntry : compiledEntries) {
                if (!compiledEntry.hideInJEI()) continue;
                var converter = new Converter();
                converter.add(compiledEntry.gameContent());
                var data = converter.convert();
                registrar.register(compiledEntry.predicate(), data);
            }
        }

        @Override
        protected void show(List<RecipeAndType<?>> show) {
            var r = Objects.requireNonNull(context).runtime.getRecipeManager();
            holders(show).forEach(h -> h.unhide(r));
        }

        @Override
        protected void hide(List<RecipeAndType<?>> hide) {
            var r = Objects.requireNonNull(context).runtime.getRecipeManager();
            holders(hide).forEach(h -> h.hide(r));
        }

        @SuppressWarnings("unchecked")
        private List<Holder<?>> holders(List<RecipeAndType<?>> recipes) {
            var map = new HashMap<RecipeType<?>, List<Object>>();
            for (var recipe : recipes) {
                map.computeIfAbsent(recipe.type, t -> new ArrayList<>()).add(recipe.recipe);
            }
            var list = new ArrayList<Holder<?>>();
            for (var entry : map.entrySet()) {
                Objects.requireNonNull(entry);
                var type = (RecipeType<Object>) entry.getKey();
                list.add(new Holder<>(type, entry.getValue()));
            }
            return list;
        }
    };

    public static void init() {
        if (instance != null) throw new IllegalStateException();
        instance = new RecipeJEI();
    }

    private RecipeJEI() {
        if (EX_DEORUM.isLoaded()) {
            ExDeorumJEIIntegration.init();
        }
        JEIAddon.RUNTIME_AVAILABLE_EVENT.addListener(this::onRuntimeAvailable);
        JEIAddon.RUNTIME_UNAVAILABLE_EVENT.addListener(this::onRuntimeUnavailable);
    }

    public void onRuntimeAvailable(JEIAddon.RuntimeAvailableEvent event) {
        var runtime = event.runtime();
        var recipeTypeByJEI = new HashMap<RecipeType<?>, net.minecraft.world.item.crafting.RecipeType<?>>();
        var recipeTypeByMinecraft = new HashMap<net.minecraft.world.item.crafting.RecipeType<?>, RecipeType<?>>();

        for (var category : runtime.getRecipeManager().createRecipeCategoryLookup().includeHidden().get().toList()) {
            Objects.requireNonNull(category);
            var type = category.getRecipeType();
            var minecraftType = BuiltInRegistries.RECIPE_TYPE.get(type.getUid());
            if (minecraftType != null) {
                recipeTypeByJEI.put(type, minecraftType);
                recipeTypeByMinecraft.put(minecraftType, type);
            }
        }

        this.context = new Context(runtime, recipeTypeByJEI, recipeTypeByMinecraft);

        reload(this.context);

        if (ClientGameStageManager.initialized()) {
            updater.fullReconfigure(ClientGameStageManager.stages());
        }
    }

    public void onRuntimeUnavailable(JEIAddon.RuntimeUnavailableEvent event) {
        this.context = null;
    }

    private void reload(Context context) {
        converterMap.clear();
    }

    @SuppressWarnings("unchecked")
    private static void convert(Context context, List<RecipeAndType<?>> list, net.minecraft.world.item.crafting.RecipeType<?> minecraftType, List<RecipeHolder<?>> recipeHolders) {
        var jeiType = context.getByMinecraft(minecraftType);
        if (jeiType == null) {
            LOGGER.error("Skipping unknown type {}", BuiltInRegistries.RECIPE_TYPE.getKey(minecraftType));
            return;
        }
        var recipeClass = jeiType.getRecipeClass();
        var recipeList = new ArrayList<>();
        for (var recipeHolder : recipeHolders) {
            if (recipeClass.isInstance(recipeHolder)) {
                recipeList.add(recipeHolder);
            } else if (recipeClass.isInstance(recipeHolder.value())) {
                recipeList.add(recipeHolder.value());
            } else if (recipeClass.isInstance(recipeHolder.id())) {
                recipeList.add(recipeHolder.id());
            } else {
                recipeList.clear();
                LOGGER.error("Failed to convert recipe holder to instance of {}, skipping recipe", recipeClass.getName());
                break;
            }
        }
        for (var o : recipeList) {
            Objects.requireNonNull(o);
            var recipeAndType = new RecipeAndType<Object>((RecipeType<Object>) jeiType, o);
            list.add(recipeAndType);
        }
    }

    private interface RecipeConverter {
        void convert(Context context, List<RecipeAndType<?>> list, net.minecraft.world.item.crafting.RecipeType<?> minecraftType, List<RecipeHolder<?>> recipeHolders);
    }

    public static RecipeManager recipeManager() {
        return Objects.requireNonNull(Minecraft.getInstance().getConnection()).getRecipeManager();
    }

    private class Converter {
        private final HashMap<net.minecraft.world.item.crafting.RecipeType<?>, List<RecipeHolder<?>>> cache = new HashMap<>();

        public void add(CommonRecipeCollection recipeCollection) {
            var recipeManager = recipeManager();
            var recipeIds = recipeCollection.recipes();
            for (var recipeId : recipeIds) {
                var recipeOptional = recipeManager.byKey(recipeId);
                if (recipeOptional.isEmpty()) {
                    LOGGER.error("No recipe for {}", recipeId, new Exception());
                }
                var recipe = recipeOptional.orElseThrow();
                var type = recipe.value().getType();
                cache.computeIfAbsent(type, ignored -> new ArrayList<>()).add(recipe);
            }
        }

        public List<RecipeAndType<?>> convert() {
            var ctx = Objects.requireNonNull(context);
            var list = new ArrayList<RecipeAndType<?>>();
            for (var entry : cache.entrySet()) {
                Objects.requireNonNull(entry);
                var type = entry.getKey();
                var recipeHolders = entry.getValue();

                var converter = converterMap.getOrDefault(type, DEFAULT_CONVERTER);
                converter.convert(ctx, list, type, recipeHolders);
            }
            return list;
        }
    }

    private record Context(IJeiRuntime runtime,
                           Map<RecipeType<?>, net.minecraft.world.item.crafting.RecipeType<?>> recipeTypeByJEI,
                           Map<net.minecraft.world.item.crafting.RecipeType<?>, RecipeType<?>> recipeTypeByMinecraft) {
        private Context {
            recipeTypeByJEI = Objects.requireNonNull(Map.copyOf(recipeTypeByJEI));
            recipeTypeByMinecraft = Objects.requireNonNull(Map.copyOf(recipeTypeByMinecraft));
        }

        public net.minecraft.world.item.crafting.@Nullable RecipeType<?> getByJEI(RecipeType<?> recipeType) {
            return recipeTypeByJEI.get(recipeType);
        }

        public @Nullable RecipeType<?> getByMinecraft(net.minecraft.world.item.crafting.RecipeType<?> recipeType) {
            return recipeTypeByMinecraft.get(recipeType);
        }
    }

    private record RecipeAndType<T>(RecipeType<T> type, T recipe) {
    }

    private record Holder<T>(mezz.jei.api.recipe.RecipeType<T> type, List<T> recipes) {
        private void unhide(IRecipeManager recipeManager) {
            recipeManager.unhideRecipes(type, recipes);
        }

        private void hide(IRecipeManager recipeManager) {
            recipeManager.hideRecipes(type, recipes);
        }
    }
}
