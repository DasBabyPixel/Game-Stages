package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addon.ClientEvents;
import de.dasbabypixel.gamestages.common.client.ClientPlayerStages;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.GameContentRegistry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.client.ContentVisibilityUpdater;
import mezz.jei.api.runtime.IJeiRuntime;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@NullMarked
public abstract class JEIVisibilityUpdater<JEIContext, OwnContext, WrapperData, RawData, Entry extends CompiledRestrictionEntry<? extends Entry, ?>> extends ContentVisibilityUpdater<WrapperData, RawData, Entry> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JEIVisibilityUpdater.class);
    private @Nullable IJeiRuntime jeiRuntime;
    private @Nullable JEIContext jeiContext;
    private @Nullable OwnContext ownContext;

    private @Nullable Contexts<JEIContext, OwnContext> contexts;

    private @Nullable ClientPlayerStages delayedRecompile = null;

    public JEIVisibilityUpdater(GameContentRegistry.Entry<?, ?, ?, ?> type) {
        super(type);
        ClientEvents.CLIENT_DISABLE.addListener(this::disable);
        JEIAddon.RUNTIME_AVAILABLE_EVENT.addListener(-100, this::onRuntimeAvailable);
        JEIAddon.RUNTIME_UNAVAILABLE_EVENT.addListener(100, this::onRuntimeUnavailable);
    }

    public void markReady(OwnContext ownContext) {
        if (this.ownContext != null) {
            markUnready();
        }
        this.ownContext = Objects.requireNonNull(ownContext);
        checkInitialize();
    }

    public void markUnready() {
        this.ownContext = null;
    }

    private void tryUnload() {
        if (contexts != null) {
            shutdown(contexts.jeiContext(), contexts.ownContext());
            contexts = null;
        }
    }

    private void disable(ClientEvents.ClientDisableEvent event) {
        jeiRuntime = null;
        delayedRecompile = null;
    }

    protected abstract JEIContext createContext(IJeiRuntime runtime);

    protected abstract void initialize(JEIContext jeiContext, OwnContext ownContext);

    protected abstract void shutdown(JEIContext jeiContext, OwnContext ownContext);

    private void checkInitialize() {
        if (jeiRuntime == null) return;
        if (ownContext == null) return;
        if (jeiContext == null) {
            jeiContext = createContext(jeiRuntime);
        }
        if (contexts == null) {
            //noinspection NullableProblems intellij is weird here idk
            contexts = new Contexts<>(jeiContext, ownContext);
            initialize(jeiContext, ownContext);

            if (delayedRecompile != null) {
                clearAndLoad(delayedRecompile);
                delayedRecompile = null;
            }
        }
    }

    private void onRuntimeAvailable(JEIAddon.RuntimeAvailableEvent event) {
        if (jeiRuntime != null) {
            LOGGER.error("JEI Runtime overridden because another was already loaded");
            tryUnload();
        }
        jeiRuntime = event.runtime();
        checkInitialize();
    }

    private void onRuntimeUnavailable(JEIAddon.RuntimeUnavailableEvent event) {
        tryUnload();

        jeiContext = null;
        jeiRuntime = null;
    }

    public JEIContext context() {
        return Objects.requireNonNull(jeiContext);
    }

    @Override
    protected void postRecompile(Addon.ClientRecompilePostEvent event) {
        if (contexts == null) {
            // Delay loading of this visibility updater
            delayedRecompile = event.stages();
            return;
        }
        delayedRecompile = null;
        clearAndLoad(event.stages());
    }

    @Override
    protected final void collect(BaseStages stages, BaseStages.CompileIndex compileIndex, Entry compiledEntry, Collector collector) {
        Objects.requireNonNull(jeiContext);
        Objects.requireNonNull(ownContext);
        collect(jeiContext, ownContext, stages, compileIndex, compiledEntry, collector);
    }

    protected abstract void collect(JEIContext jeiContext, OwnContext ownContext, BaseStages stages, BaseStages.CompileIndex compileIndex, Entry compiledEntry, Collector collector);

    private record Contexts<JEIContext, OwnContext>(JEIContext jeiContext, OwnContext ownContext) {
    }
}
