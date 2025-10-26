package de.dasbabypixel.gamestages.common.integration.kubejs.api.server;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;

import java.util.function.Consumer;

public class GameStagesJS {
    public static GameStage registerStage(String name) {
        var stage = new GameStage(name);
        System.out.println("Register stage " + name);
        ServerGameStageManager.instance().add(stage);
        return stage;
    }

    public static void configureStage(String name, Consumer<GameStage> configurer) {
        var stage = ServerGameStageManager.instance().get(name);
        if (stage != null) configurer.accept(stage);
    }

    public static void test() {
        System.out.println("test 1");
    }

    public static void test2() {
        System.out.println("test 2");
    }

    public static void test3(String input) {
        System.out.println("test 3: " + input);
    }
}
