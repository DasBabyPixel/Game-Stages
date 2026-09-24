package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameContentMod;
import de.dasbabypixel.gamestages.common.data.GameContentUnion;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ServerMutableGameStageManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventJSBase;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.GameCollectionJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.GameCollectionJSImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.ModIdJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.ModIdJSImpl;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Objects;

@NullMarked
public final class ServerRegisterEventJS extends EventJSBase<ServerRegisterEventJS> {
    public static final EventType<ServerRegisterEventJS> TYPE = new EventType<>(ServerRegisterEventJS.class);

    static {
        TYPE.addFunction("registerStage", (call, cx, args) -> {
            var stage = new GameStage((String) args[0]);
            call.event().stageManager().add(stage);
            return stage;
        }, GameStage.class, String.class);
        TYPE.addFunctionVarArgs("mods", (event, cx, args) -> {
            var mods = (ModIdJS[]) args[0];
            var list = new ArrayList<GameContent>();
            for (var mod : mods) {
                Objects.requireNonNull(mod);
                list.add(new GameContentMod(((ModIdJSImpl) mod).modId()));
            }
            return new GameCollectionJSImpl(cx, GameContentUnion.create(list));
        }, GameCollectionJS.class, ModIdJS[].class);
    }

    private final ServerMutableGameStageManager stageManager;

    public ServerRegisterEventJS(ServerMutableGameStageManager stageManager) {
        super(TYPE);
        this.stageManager = stageManager;
    }

    public ServerMutableGameStageManager stageManager() {
        return stageManager;
    }
}
