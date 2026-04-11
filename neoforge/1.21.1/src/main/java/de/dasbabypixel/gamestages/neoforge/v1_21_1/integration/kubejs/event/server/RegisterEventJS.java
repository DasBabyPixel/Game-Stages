package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.CollectionWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.ModContentWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventJSBase;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class RegisterEventJS extends EventJSBase<RegisterEventJS> {
    public static final EventType<RegisterEventJS> TYPE = new EventType<>(RegisterEventJS.class);
    private final AbstractGameStageManager<?> stageManager;

    static {
        TYPE.addFunction("registerStage", (event, cx, args) -> {
            var stage = new GameStage((String) args[0]);
            event.stageManager.add(stage);
            return stage;
        }, GameStage.class, String.class);
        TYPE.addFunctionVarArgs("mods", (event, cx, args) -> args[0], CollectionWrapper.class, CollectionWrapper.class, ModContentWrapper[].class);
    }

    public RegisterEventJS(AbstractGameStageManager<?> stageManager) {
        super(TYPE);
        this.stageManager = stageManager;
    }

    public AbstractGameStageManager<?> stageManager() {
        return stageManager;
    }
}
