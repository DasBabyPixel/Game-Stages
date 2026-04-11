package de.dasbabypixel.gamestages.neoforge.v1_21_1.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class StageArgumentType implements ArgumentType<StageArgumentType.Provider> {
    private static final DynamicCommandExceptionType UNKNOWN_STAGE = new DynamicCommandExceptionType(arg1 -> Component.literal("Unknown stage: " + arg1));
    private final boolean enforceExistence;

    public StageArgumentType(boolean enforceExistence) {
        this.enforceExistence = enforceExistence;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        AbstractGameStageManager manager;
        if (FMLEnvironment.dist.isClient()) {
            if (context.getSource() instanceof ClientSuggestionProvider) {
                manager = ClientGameStageManager.instance();
            } else {
                manager = ServerGameStageManager.instance();
            }
        } else {
            manager = ServerGameStageManager.instance();
        }

        if (enforceExistence) {
            return SharedSuggestionProvider.suggest(manager.gameStages().stream().map(GameStage::name), builder);
        }

        return Objects.requireNonNull(builder.buildFuture());
    }

    @Override
    public Provider parse(StringReader reader) throws CommandSyntaxException {
        var stageName = reader.readString();

        return context -> {
            var stage = new GameStage(stageName);
            if (!enforceExistence) return stage;

            AbstractGameStageManager manager;

            if (FMLEnvironment.dist.isClient()) {
                if (context.getSource() instanceof ClientSuggestionProvider) {
                    manager = ClientGameStageManager.instance();
                } else {
                    manager = ServerGameStageManager.instance();
                }
            } else {
                manager = ServerGameStageManager.instance();
            }

            if (manager.gameStages().contains(stage)) {
                return stage;
            }
            throw UNKNOWN_STAGE.createWithContext(reader, stageName);
        };
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
