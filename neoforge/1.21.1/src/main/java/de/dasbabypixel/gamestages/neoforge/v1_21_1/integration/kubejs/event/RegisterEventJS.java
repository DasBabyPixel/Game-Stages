package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContent;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSParserBase;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

public final class RegisterEventJS extends EventJSBase<RegisterEventJS> {
    public static final EventType<RegisterEventJS> TYPE = new EventType<>(RegisterEventJS.class);

    static {
        TYPE.addFunction("registerStage", (event, cx, args) -> {
            var stage = new GameStage(cx.toString(args[0]));
            event.stageManager.add(stage);
            return stage;
        });
        TYPE.addFunction("mods", (event, cx, args) -> JSParserBase.parse(Arrays
                .stream(args)
                .map(cx::toString)
                .map(CommonGameContent.Mod::new)
                .toList()));
    }

    private final @NonNull AbstractGameStageManager stageManager;

    public RegisterEventJS(@NonNull AbstractGameStageManager stageManager) {
        super(TYPE);
        this.stageManager = stageManager;
    }

    public @NonNull AbstractGameStageManager stageManager() {
        return stageManager;
    }
}
