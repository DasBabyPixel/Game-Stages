package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.event.EventType;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.RecipeContentWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.client.ContentVisibilityUpdater;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class RecipeVisibilityUpdater extends ContentVisibilityUpdater<RecipeVisibilityUpdater.Wrapper, RecipeAndType<?>, CommonRecipeRestrictionEntry.Compiled> {
    public static final EventType<RegisterConverters> REGISTER_CONVERTERS_EVENT = EventType.create();
    private static final Logger LOGGER = LoggerFactory.getLogger(RecipeVisibilityUpdater.class);
    private static final DefaultConverter DEFAULT_CONVERTER = new DefaultConverter();
    private final Map<net.minecraft.world.item.crafting.RecipeType<?>, ConverterEntry<?, ?, ?>> converterMap = new HashMap<>();
    private final RecipeJEI recipeJEI;
    private Map<RecipeAndType<?>, ConverterEntry<?, ?, ?>> converterOrigins = Map.of();

    public RecipeVisibilityUpdater(RecipeJEI recipeJEI) {
        super(de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.RecipeType.get());
        this.recipeJEI = recipeJEI;
    }

    @SuppressWarnings("unchecked")
    public void reload(Context context) {
        converterMap.clear();
        REGISTER_CONVERTERS_EVENT.call(new RegisterConverters(context));
        for (var e : context.recipeTypeByMinecraft().entrySet()) {
            Objects.requireNonNull(e);
            var mcType = (net.minecraft.world.item.crafting.RecipeType<Recipe<RecipeInput>>) e.getKey();
            if (converterMap.containsKey(mcType)) continue;
            converterMap.put(mcType, new ConverterEntry<>(mcType, DEFAULT_CONVERTER, context));
        }
    }

    @Override
    protected void preCollect() {
        converterOrigins = new HashMap<>();
    }

    @Override
    protected void postCollect() {
        converterOrigins = Map.copyOf(converterOrigins);
    }

    @Override
    protected boolean shouldBeVisible(Wrapper wrapper) {
        return wrapper.visible();
    }

    @Override
    protected void invalidateCache(Collection<Wrapper> affected) {
        var byConverters = new HashMap<ConverterEntry<?, ?, ?>, List<Wrapper>>();
        for (var wrapper : affected) {
            wrapper.isCached = false;
            byConverters.computeIfAbsent(wrapper.converter, s -> new ArrayList<>()).add(wrapper);
        }
        for (var e : byConverters.entrySet()) {
            Objects.requireNonNull(e);
            e.getKey().invalidate(e.getValue());
        }
    }

    @Override
    protected void collect(BaseStages stages, BaseStages.CompileIndex compileIndex, CommonRecipeRestrictionEntry.Compiled compiledEntry, Collector collector) {
        var predicate = compiledEntry.predicate();
        if (!compiledEntry.hideInJEI()) predicate = CompiledRestrictionPredicate.TRUE;
        var converter = new Converter();
        converter.add(compiledEntry.gameContent());
        var converted = converter.convert();
        converterOrigins.putAll(converted.converterEntryMap);
        for (var recipeAndType : converted.recipes) {
            collector.add(predicate, recipeAndType);
        }
    }

    @Override
    protected void show(List<RecipeAndType<?>> show) {
        var r = recipeJEI.context().runtime().getRecipeManager();
        holders(show).forEach(h -> h.unhide(r));
    }

    @Override
    protected void hide(List<RecipeAndType<?>> hide) {
        var r = recipeJEI.context().runtime().getRecipeManager();
        holders(hide).forEach(h -> h.hide(r));
    }

    @Override
    protected RecipeAndType<?> extract(Wrapper wrapper) {
        return wrapper.recipeAndType;
    }

    @Override
    protected Wrapper createWrapper(RecipeAndType<?> recipeAndType, List<CompiledRestrictionPredicate> relevantEntries) {
        var converter = Objects.requireNonNull(converterOrigins.get(recipeAndType));
        if (relevantEntries.contains(CompiledRestrictionPredicate.TRUE))
            return new Wrapper(recipeAndType, converter, List.of(CompiledRestrictionPredicate.TRUE));
        return new Wrapper(recipeAndType, converter, relevantEntries);
    }

    @SuppressWarnings("unchecked")
    private List<Holder<?>> holders(List<RecipeAndType<?>> recipes) {
        var map = new HashMap<RecipeType<?>, List<Object>>();
        for (var recipe : recipes) {
            map.computeIfAbsent(recipe.type(), t -> new ArrayList<>()).add(recipe.recipe());
        }
        var list = new ArrayList<Holder<?>>();
        for (var entry : map.entrySet()) {
            Objects.requireNonNull(entry);
            var type = (RecipeType<Object>) entry.getKey();
            list.add(new Holder<>(type, entry.getValue()));
        }
        return list;
    }

    public static class Wrapper {
        private final RecipeAndType<?> recipeAndType;
        private final ConverterEntry<?, ?, ?> converter;
        private final List<CompiledRestrictionPredicate> predicate;
        private boolean isCached;
        private boolean cachedVisible;

        private Wrapper(RecipeAndType<?> recipeAndType, ConverterEntry<?, ?, ?> converter, List<CompiledRestrictionPredicate> predicate) {
            this.recipeAndType = recipeAndType;
            this.converter = converter;
            this.predicate = predicate;
        }

        public boolean visible() {
            if (isCached) {
                return cachedVisible;
            }
            isCached = true;
            cachedVisible = false;
            for (var p : predicate) {
                if (p.test()) {
                    cachedVisible = true;
                    break;
                }
            }
            return cachedVisible;
        }
    }

    private static class ConverterEntry<R extends Recipe<I>, I extends RecipeInput, Cache> {
        private final net.minecraft.world.item.crafting.RecipeType<R> minecraftType;
        private final RecipeConverter<R, I, Cache> recipeConverter;
        private final Context context;
        private final Cache cache;

        public ConverterEntry(net.minecraft.world.item.crafting.RecipeType<R> minecraftType, RecipeConverter<R, I, Cache> recipeConverter, Context context) {
            this.minecraftType = minecraftType;
            this.recipeConverter = recipeConverter;
            this.context = context;
            this.cache = recipeConverter.buildCache(context, minecraftType);
        }

        @SuppressWarnings("unchecked")
        private void convert(List<RecipeAndType<?>> list, List<? extends RecipeHolder<?>> recipeHolders) {
            recipeConverter.convert(context, list, minecraftType, (List<RecipeHolder<R>>) recipeHolders, cache);
        }

        private void invalidate(List<Wrapper> wrappers) {
            var list = new ArrayList<RecipeAndType<?>>();
            wrappers.forEach(w -> list.add(w.recipeAndType));
            recipeConverter.invalidate(List.copyOf(list));
        }
    }

    private record Holder<T>(mezz.jei.api.recipe.RecipeType<T> type, List<T> recipes) {
        private void unhide(IRecipeManager recipeManager) {
            recipeManager.unhideRecipes(type, recipes);
        }

        private void hide(IRecipeManager recipeManager) {
            recipeManager.hideRecipes(type, recipes);
        }
    }

    private record Converted(List<RecipeAndType<?>> recipes,
                             Map<RecipeAndType<?>, ConverterEntry<?, ?, ?>> converterEntryMap) {
        private Converted {
            recipes = List.copyOf(recipes);
            converterEntryMap = Map.copyOf(converterEntryMap);
        }
    }

    public final class RegisterConverters {
        private final Context context;

        private RegisterConverters(Context context) {
            this.context = context;
        }

        public <T extends Recipe<I>, I extends RecipeInput> void register(net.minecraft.world.item.crafting.RecipeType<T> recipeType, RecipeConverter<T, I, ?> recipeConverter) {
            var entry = new ConverterEntry<>(recipeType, recipeConverter, context);
            converterMap.put(recipeType, entry);
        }

        public Context context() {
            return context;
        }
    }

    private class Converter {
        private final HashMap<net.minecraft.world.item.crafting.RecipeType<?>, List<RecipeHolder<?>>> cache = new HashMap<>();

        public void add(RecipeContentWrapper gameContent) {
            var recipeManager = RecipeJEI.recipeManager();
            var recipeIds = gameContent.gameContent().content();
            for (var recipeId : recipeIds) {
                var recipeOptional = recipeManager.byKey(recipeId);
                if (recipeOptional.isEmpty()) {
                    LOGGER.error("No recipe for {}", recipeId, new Exception());
                    continue;
                }
                var recipe = recipeOptional.orElseThrow();
                var type = recipe.value().getType();
                cache.computeIfAbsent(type, ignored -> new ArrayList<>()).add(recipe);
            }
        }

        @SuppressWarnings("unchecked")
        public Converted convert() {
            var list = new ArrayList<RecipeAndType<?>>();
            var ceMap = new HashMap<RecipeAndType<?>, ConverterEntry<?, ?, ?>>();
            for (var entry : cache.entrySet()) {
                Objects.requireNonNull(entry);
                var type = (net.minecraft.world.item.crafting.RecipeType<Recipe<RecipeInput>>) entry.getKey();
                var recipeHolders = List.copyOf(entry.getValue());
                var e = converterMap.get(type);
                if (e == null) continue;
                var l = new ArrayList<RecipeAndType<?>>();
                e.convert(l, recipeHolders);
                list.addAll(l);
                for (var recipeAndType : l) {
                    ceMap.put(recipeAndType, e);
                }
            }
            return new Converted(list, ceMap);
        }
    }
}
