package de.dasbabypixel.gamestages.neoforge.v1_21_1.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.server.GlobalServerState;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@NullMarked
public class StagesArgumentType implements ArgumentType<StagesArgumentType.Provider> {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_ALL = (context, builder) -> {
        var manager = manager(context);
        return SharedSuggestionProvider.suggest(manager.gameStages().stream().map(GameStage::name), builder);
    };
    private static final DynamicCommandExceptionType UNKNOWN_STAGE = new DynamicCommandExceptionType(arg1 -> Component.literal("Unknown stage: " + arg1));
    private final boolean enforceExistence;

    public StagesArgumentType(boolean enforceExistence) {
        this.enforceExistence = enforceExistence;
    }

    public static SuggestionProvider<CommandSourceStack> suggestMissingPlayers(String playersArgumentName) {
        return (context, builder) -> {
            var players = EntityArgument.getPlayers(context, playersArgumentName);
            var stages = new HashSet<>(manager(context).gameStages());
            var removeStages = new HashSet<>(manager(context).gameStages());
            for (var player : players) {
                Objects.requireNonNull(player);
                removeStages.retainAll(player.getGameStages().getAll());
            }
            stages.removeAll(removeStages);
            return SharedSuggestionProvider.suggest(stages.stream().map(GameStage::name), builder);
        };
    }

    public static SuggestionProvider<CommandSourceStack> suggestExistingPlayers(String playersArgumentName) {
        return (context, builder) -> {
            var players = EntityArgument.getPlayers(context, playersArgumentName);
            var stages = new HashSet<GameStage>();
            for (var player : players) {
                Objects.requireNonNull(player);
                stages.addAll(player.getGameStages().getAll());
            }
            return SharedSuggestionProvider.suggest(stages.stream().map(GameStage::name), builder);
        };
    }

    private static AbstractGameStageManager<?> manager(CommandContext<?> context) {
        if (FMLEnvironment.dist.isClient()) {
            if (context.getSource() instanceof ClientSuggestionProvider) {
                return ClientGameStageManager.currentManager();
            } else {
                return GlobalServerState.currentManager();
            }
        } else {
            return GlobalServerState.currentManager();
        }
    }

    public static List<GameStage> getStage(CommandContext<?> ctx, String name) throws CommandSyntaxException {
        return Objects.requireNonNull(Objects.requireNonNull(ctx.getArgument(name, Provider.class)).getStage(ctx));
    }

    public static boolean isAllowedInUnquotedString(final char c) {
        return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_' || c == '-' || c == '.' || c == '+' || c == '*';
    }

    public String readUnquotedString(StringReader reader) {
        final int start = reader.getCursor();
        while (reader.canRead() && isAllowedInUnquotedString(reader.peek())) {
            reader.skip();
        }
        return Objects.requireNonNull(reader.getString()).substring(start, reader.getCursor());
    }

    @Override
    public Provider parse(@Nullable StringReader reader) throws CommandSyntaxException {
        assert reader != null;
        var stagePattern = readUnquotedString(reader);

        return context -> {
            if (stagePattern.endsWith("*")) {
                var prefix = stagePattern.substring(0, stagePattern.length() - 1);
                var manager = manager(context);
                return List.copyOf(manager.gameStages().stream().filter(s -> s.name().startsWith(prefix)).toList());
            } else {
                var stage = new GameStage(stagePattern);
                if (!enforceExistence) return List.of(stage);

                var manager = manager(context);
                if (manager.gameStages().contains(stage)) {
                    return List.of(stage);
                }
            }
            throw Objects.requireNonNull(UNKNOWN_STAGE.createWithContext(reader, stagePattern));
        };
    }

    public interface Provider {
        List<GameStage> getStage(CommandContext<?> context) throws CommandSyntaxException;
    }

    public static class Info implements ArgumentTypeInfo<StagesArgumentType, Info.ITemplate> {
        @Override
        public void serializeToNetwork(ITemplate iTemplate, FriendlyByteBuf friendlyByteBuf) {
            friendlyByteBuf.writeBoolean(iTemplate.enforceExistence);
        }

        @Override
        public ITemplate deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
            return new ITemplate(friendlyByteBuf.readBoolean());
        }

        @Override
        public void serializeToJson(ITemplate iTemplate, JsonObject jsonObject) {
            jsonObject.addProperty("enforce_existence", iTemplate.enforceExistence);
        }

        @Override
        public ITemplate unpack(StagesArgumentType stageArgumentType) {
            return new ITemplate(stageArgumentType.enforceExistence);
        }

        public class ITemplate implements ArgumentTypeInfo.Template<StagesArgumentType> {
            private final boolean enforceExistence;

            public ITemplate(boolean enforceExistence) {
                this.enforceExistence = enforceExistence;
            }

            @Override
            public StagesArgumentType instantiate(CommandBuildContext commandBuildContext) {
                return new StagesArgumentType(enforceExistence);
            }

            @Override
            public ArgumentTypeInfo<StagesArgumentType, ?> type() {
                return Info.this;
            }
        }
    }
}
