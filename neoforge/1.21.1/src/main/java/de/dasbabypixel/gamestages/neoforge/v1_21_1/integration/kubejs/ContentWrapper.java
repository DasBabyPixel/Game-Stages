package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ContentWrapper {
    @HideFromJS
    GameContent content();

    default ContentWrapper except(GameContent... other) {
        return new SomeContentWrapper(content().except(other));
    }

    default ContentWrapper only(GameContent... other) {
        return new SomeContentWrapper(content().only(other));
    }

    default ContentWrapper union(GameContent... other) {
        return new SomeContentWrapper(content().union(other));
    }

    default ContentWrapper filterType(GameContentType<?> type) {
        return new SomeContentWrapper(content().filterType(type));
    }
}
