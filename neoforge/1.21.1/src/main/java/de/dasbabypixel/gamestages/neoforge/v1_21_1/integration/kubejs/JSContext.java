package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.data.GameContentRegistry;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleAttributeHolder;
import de.dasbabypixel.gamestages.common.data.manager.mutable.SimpleMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.GameCollectionTypeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.GameCollectionTypeJSImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.TypedGameCollectionJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.KubeJSServerContext;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.Context;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public final class JSContext extends SimpleAttributeHolder<JSContext> {
    private final KubeJSContext context;

    public JSContext(KubeJSContext context) {
        this.context = context;
    }

    public static JSContext instance(@Nullable Context context) {
        return manager(Objects.requireNonNull(context)).context();
    }

    public static void initInstance(Context context) {
        manager(context).context(new JSContext((KubeJSContext) context));
    }

    public static void clearInstance(Context context) {
        manager(context).context(null);
    }

    private static StagesServerScriptManager manager(Context context) {
        return ((StagesServerScriptManager) Objects.requireNonNull(((KubeJSServerContext) context).kjsFactory).manager);
    }

    public RestrictionEntryOrigin origin() {
        return RestrictionEntryOrigin.string(Objects.requireNonNull(source().toString()));
    }

    public SourceLine source() {
        return Objects.requireNonNull(SourceLine.of(context));
    }

    public KubeJSContext context() {
        return context;
    }

    public interface TypedContentParser {
        TypedGameCollectionJS parse(Context context, Object... args);
    }

    public static class Attributes {
        public static final SimpleAttribute<JSContext, SimpleMutableGameStageManager<?, ?>> STAGE_MANAGER = new SimpleAttribute<>();
        public static final SimpleAttribute<JSContext, ContentTypes> CONTENT_TYPES = new SimpleAttribute<>();
    }

    public static class ContentTypes {
        private final Map<String, GameCollectionTypeJS> typeById;

        public ContentTypes(Collection<TypeEntry> types) {
            var typeById = new HashMap<String, GameCollectionTypeJS>();
            for (var type : types) {
                typeById.put(type.typeEntry.id(), new GameCollectionTypeJSImpl(type.typeEntry, type.contentParser));
            }
            this.typeById = Map.copyOf(typeById);
        }

        public GameCollectionTypeJS type(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry) {
            return Objects.requireNonNull(typeById.get(typeEntry.id()));
        }

        public GameCollectionTypeJS typeById(String id) {
            return Objects.requireNonNull(typeById.get(id), () -> "Unknown type: " + id);
        }
    }

    public record TypeEntry(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry, TypedContentParser contentParser) {
    }
}
