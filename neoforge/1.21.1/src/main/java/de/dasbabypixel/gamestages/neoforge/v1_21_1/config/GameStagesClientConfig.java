package de.dasbabypixel.gamestages.neoforge.v1_21_1.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("DataFlowIssue")
@NullMarked
public class GameStagesClientConfig {
    public static final GameStagesClientConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;
    public final ModConfigSpec.BooleanValue exdeorumOverrideJEI;

    static {
        var pair = new ModConfigSpec.Builder().configure(GameStagesClientConfig::new);

        CONFIG = Objects.requireNonNull(pair.getLeft());
        CONFIG_SPEC = Objects.requireNonNull(pair.getRight());
    }

    private GameStagesClientConfig(ModConfigSpec.Builder builder) {
        exdeorumOverrideJEI = builder.define(List.of("exdeorum", "override_jei"), true);
    }
}
