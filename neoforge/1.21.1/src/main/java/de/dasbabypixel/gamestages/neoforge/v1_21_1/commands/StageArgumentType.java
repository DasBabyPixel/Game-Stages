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

import java.util.HashSet;
import java.util.Objects;

@NullMarked
public class StageArgumentType implements ArgumentType<StageArgumentType.Provider> {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_ALL = (context, builder) -> {
        var manager = manager(context);
        return SharedSuggestionProvider.suggest(manager.gameStages().stream().map(GameStage::name), builder);
    };
    private static final DynamicCommandExceptionType UNKNOWN_STAGE = new DynamicCommandExceptionType(arg1 -> Component.literal("Unknown stage: " + arg1));
    private final boolean enforceExistence;

    public StageArgumentType(boolean enforceExistence) {
        this.enforceExistence = enforceExistence;
    }

    @Override
    public Provider parse(StringReader reader) throws CommandSyntaxException {
        var stageName = reader.readString();

        return context -> {
            var stage = new GameStage(stageName);
            if (!enforceExistence) return stage;
            var manager = manager(context);
            if (manager.gameStages().contains(stage)) {
                return stage;
            }
            throw UNKNOWN_STAGE.createWithContext(reader, stageName);
        };
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

    public static GameStage getStage(CommandContext<?> ctx, String name) throws CommandSyntaxException {
        return Objects.requireNonNull(Objects.requireNonNull(ctx.getArgument(name, Provider.class)).getStage(ctx));
    }

    public interface Provider {
        GameStage getStage(CommandContext<?> context) throws CommandSyntaxException;
    }

    public static class Info implements ArgumentTypeInfo<StageArgumentType, Info.ITemplate> {
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
        public ITemplate unpack(StageArgumentType stageArgumentType) {
            return new ITemplate(stageArgumentType.enforceExistence);
        }

        public class ITemplate implements ArgumentTypeInfo.Template<StageArgumentType> {
            private final boolean enforceExistence;

            public ITemplate(boolean enforceExistence) {
                this.enforceExistence = enforceExistence;
            }

            @Override
            public StageArgumentType instantiate(CommandBuildContext commandBuildContext) {
                return new StageArgumentType(enforceExistence);
            }

            @Override
            public ArgumentTypeInfo<StageArgumentType, ?> type() {
                return Info.this;
            }
        }
    }
}
