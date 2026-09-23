package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameContentDirect;
import de.dasbabypixel.gamestages.common.data.GameContentMod;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.RecipeType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSContext;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSParserBase;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.TypedGameCollectionJS;
import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.rhino.Context;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

@NullMarked
public class RecipeJSParser extends JSParserBase {
    public RecipeJSParser() {
        registerHandler(RecipeLikeKJS.class, (value, parseAppender) -> value.kjs$getOrCreateId());
        registerHandler(ResourceLocation.class, new CollectingHandler<ResourceLocation, ResourceLocation>() {
            @Override
            public ResourceLocation transform(ResourceLocation value) {
                return value;
            }

            @Override
            public GameContent finish(Set<ResourceLocation> set) {
                return new GameContentDirect<>(RecipeType.get(), List.copyOf(set));
            }
        });
        registerHandler(CharSequence.class, (value, parseAppender) -> {
            var string = value.toString();
            if (string.startsWith("@")) {
                return new GameContentMod(string.substring(1)).filterType(RecipeType.get());
            }
            if (string.startsWith(".")) string = string.substring(1);
            return ResourceLocation.parse(string);
        });
    }

    @Override
    public TypedGameCollectionJS parse(Context cx, Object @Nullable ... inputs) {
        var c = super.parse(cx, inputs);
        var type = JSContext.instance(cx).get(JSContext.Attributes.CONTENT_TYPES).type(RecipeType.get());
        return c.filterType(type);
    }
}
