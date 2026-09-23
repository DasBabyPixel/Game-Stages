package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi;

import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSContext;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TypedGameCollectionJSImpl extends GameCollectionJSImpl implements TypedGameCollectionJS {
    private final JSContext context;
    private final GameCollectionTypeJSImpl type;
    private final TypedGameContent<?, ?, ?> gameContent;

    public TypedGameCollectionJSImpl(JSContext context, GameCollectionTypeJSImpl type, TypedGameContent<?, ?, ?> gameContent) {
        super(context, gameContent);
        this.context = context;
        this.type = type;
        this.gameContent = gameContent;
    }

    public TypedGameCollectionJS except(Context context, Object... other) {
        return except(type.parser().parse(context, other));
    }

    public TypedGameCollectionJS only(Context context, Object... other) {
        return only(type.parser().parse(context, other));
    }

    @HideFromJS
    @Override
    public TypedGameCollectionJS except(GameCollectionJS... other) {
        return new TypedGameCollectionJSImpl(context, type, gameContent.except(convert(other)));
    }

    @HideFromJS
    @Override
    public TypedGameCollectionJS only(GameCollectionJS... other) {
        return new TypedGameCollectionJSImpl(context, type, gameContent.only(convert(other)));
    }

    @Override
    public TypedGameCollectionJS filterType(GameCollectionTypeJS type_) {
        var type = ((GameCollectionTypeJSImpl) type_);
        if (type == this.type) return this;
        return new TypedGameCollectionJSImpl(context, type, type.typeEntry().empty());
    }

    @Override
    public GameCollectionTypeJSImpl type() {
        return type;
    }
}
