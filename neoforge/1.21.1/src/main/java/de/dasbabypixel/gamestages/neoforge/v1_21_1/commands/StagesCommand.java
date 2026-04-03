package de.dasbabypixel.gamestages.neoforge.v1_21_1.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.VItemAddon;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class StagesCommand {
    public static void register(@NonNull CommandDispatcher<CommandSourceStack> dispatcher) {
        var cmd = Commands.literal("stages");

        // @formatter:off
        cmd.then(Commands.literal("add")
                .then(Commands.argument("target", EntityArgument.players())
                        .then(Commands.argument("stage", new StageArgumentType(true))
                                .executes(StagesCommand::addTargetStage)
                        )
                )
        );
        cmd.then(Commands.literal("remove")
                .then(Commands.argument("target", EntityArgument.players())
                        .then(Commands.argument("stage", new StageArgumentType(false))
                                .suggests((context, builder) -> {
                                    // @formatter:on
                                    Collection<net.minecraft.server.level.@NonNull ServerPlayer> targets = EntityArgument.getPlayers(context, "target");
                                    return SharedSuggestionProvider.suggest(targets
                                            .stream()
                                            .map(ServerPlayer::getGameStages)
                                            .map(BaseStages::getAll)
                                            .flatMap(Set::stream)
                                            .map(GameStage::name)
                                            .distinct(), builder);
                                    // @formatter:off
                                })
                                .executes(StagesCommand::removeTargetStage)
                        )
                )
        );
        cmd.then(Commands.literal("list")
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(StagesCommand::listTarget)
                )
        );
        // TODO move to item addon
        cmd.then(Commands.literal("hand")
                .executes(StagesCommand::hand)
        );
        // @formatter:on

        dispatcher.register(cmd);
    }

    // TODO move to item addon
    private static int hand(@NonNull CommandContext<@NonNull CommandSourceStack> context) throws CommandSyntaxException {
        var player = Objects.requireNonNull(context.getSource().getPlayer());
        var stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        var entry = VItemAddon.getEntry(player.getGameStages(), stack, stack);
        player.sendSystemMessage(Component.literal(entry == null ? "No restriction" : (entry
                                                                                       .predicate()
                                                                                       .predicate() + " -> " + entry
                                                                                                               .predicate()
                                                                                                               .test())));
        return 0;
    }

    private static int listTarget(@NonNull CommandContext<@NonNull CommandSourceStack> context) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(context, "target");
        var stages = Set.copyOf(player.getGameStages().getAll());
        context.getSource().sendSuccess(() -> {
            var c = Component.empty().append(player.getName()).append(Component.literal(": (" + stages.size() + ")"));
            for (var stage : stages) {
                c = c.append("\n - " + stage.name());
            }
            return c;
        }, true);
        return 0;
    }

    private static int addTargetStage(@NonNull CommandContext<@NonNull CommandSourceStack> context) throws CommandSyntaxException {
        var players = EntityArgument.getPlayers(context, "target");
        var stage = StageArgumentType.getStage(context, "stage");
        var cnt = 0;
        for (@NonNull Player player : players) {
            if (player.getGameStages().add(stage)) cnt++;
        }
        var fcnt = cnt;
        context
                .getSource()
                .sendSuccess(() -> Component.literal("Added stage " + stage + " to " + fcnt + " players"), true);
        return 0;
    }

    private static int removeTargetStage(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var players = EntityArgument.getPlayers(context, "target");
        var stage = StageArgumentType.getStage(context, "stage");
        var cnt = 0;
        for (Player player : players) {
            if (player.getGameStages().remove(stage)) cnt++;
        }
        var fcnt = cnt;
        context
                .getSource()
                .sendSuccess(() -> Component.literal("Removed stage " + stage + " from " + fcnt + " players"), true);
        return 0;
    }
}
