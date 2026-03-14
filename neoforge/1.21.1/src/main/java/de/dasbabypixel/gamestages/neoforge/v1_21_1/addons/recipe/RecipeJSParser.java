package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContent;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSParserBase;
import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Set;

public class RecipeJSParser extends JSParserBase {
    public RecipeJSParser() {
        registerHandler(RecipeLikeKJS.class, (value, parseAppender) -> value.kjs$getOrCreateId());
        registerHandler(ResourceLocation.class, new CollectingHandler<ResourceLocation, ResourceLocation>() {
            @Override
            public ResourceLocation transform(ResourceLocation value) {
                return value;
            }

            @Override
            public @NonNull CommonGameContent finish(Set<ResourceLocation> set) {
                return new CommonRecipeCollection(List.copyOf(set));
            }
        });
        registerHandler(CharSequence.class, (value, parseAppender) -> {
            var string = value.toString();
            if (string.startsWith("@")) {
                return new CommonGameContent.Mod(string.substring(1)).filterType(CommonRecipeCollection.TYPE);
            }
            if (string.startsWith(".")) string = string.substring(1);
            return ResourceLocation.parse(string);
        });
    }
}
