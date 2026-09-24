package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameContentUnion;
import de.dasbabypixel.gamestages.common.data.GameContentWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSContext;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@NullMarked
public class GameCollectionJSImpl implements GameCollectionJS, GameContentWrapper {
    private final JSContext context;
    private final GameContent gameContent;

    public GameCollectionJSImpl(JSContext context, GameContent gameContent) {
        this.context = context;
        this.gameContent = gameContent;
    }

    @SafeVarargs
    static <JS extends GameCollectionJS> GameContent[] convert(JS... args) {
        return Arrays
                .stream(args)
                .map(s -> (GameCollectionJSImpl) s)
                .map(GameCollectionJSImpl::gameContent)
                .toArray(GameContent[]::new);
    }

    public static GameCollectionJS union(JSContext context, List<? extends GameCollectionJS> list) {
        return new GameCollectionJSImpl(context, GameContentUnion.create(Arrays.asList(convert(Objects.requireNonNull(list.toArray(GameCollectionJS[]::new))))));
    }

    @Override
    public String toString() {
        return gameContent.toString();
    }

    @Override
    public GameCollectionJS except(GameCollectionJS... other) {
        return new GameCollectionJSImpl(context, gameContent.except(convert(other)));
    }

    @Override
    public GameCollectionJS only(GameCollectionJS... other) {
        return new GameCollectionJSImpl(context, gameContent.only(convert(other)));
    }

    @Override
    public GameCollectionJS union(GameCollectionJS... other) {
        return new GameCollectionJSImpl(context, gameContent.union(convert(other)));
    }

    @Override
    public TypedGameCollectionJS filterType(GameCollectionTypeJS type) {
        var typeEntry = CommonInstances.gameContentRegistry.byTypeId(type.toString());
        return new TypedGameCollectionJSImpl(context, (GameCollectionTypeJSImpl) type, gameContent.filterType(typeEntry));
    }

    public JSContext context() {
        return context;
    }

    @Override
    public GameContent gameContent() {
        return gameContent;
    }
}
