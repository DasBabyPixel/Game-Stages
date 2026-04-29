package de.dasbabypixel.gamestages.neoforge.v1_21_1.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.VItemAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.entity.IBlockEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@NullMarked
public class StagesCommand {
    private static final DynamicCommandExceptionType UNSUPPORTED_SOURCE = new DynamicCommandExceptionType(o -> Component.literal("Unsupported source: " + o));

    @SuppressWarnings("DataFlowIssue")
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var cmd = Commands.literal("stages");

        // @formatter:off
        cmd.then(Commands.literal("add")
                .then(Commands.argument("target", EntityArgument.players())
                        .then(Commands.argument("stage", new StageArgumentType(true))
                                .suggests(StageArgumentType.suggestMissingPlayers("target"))
                                .executes(StagesCommand::addTargetStage)
                        )
                )
        );
        cmd.then(Commands.literal("remove")
                .then(Commands.argument("target", EntityArgument.players())
                        .then(Commands.argument("stage", new StageArgumentType(false))
                                .suggests(StageArgumentType.suggestExistingPlayers("target"))
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
                .requires(CommandSourceStack::isPlayer)
                .executes(StagesCommand::hand)
        );
        cmd.then(Commands.literal("recipe")
                .then(Commands.argument("recipe", ResourceLocationArgument.id())
                        .suggests(SuggestionProviders.ALL_RECIPES)
                        .executes(ctx -> {
                            var stages = stagesOf(ctx.getSource());
                            var recipe = ResourceLocationArgument.getRecipe(ctx, "recipe");
                            var entry = VRecipeAddon.getEntry(stages, recipe);
                            var msg = entry == null ? "No restriction" : (entry.predicate().predicate() + " -> " + entry.predicate().test());
                            ctx.getSource().sendSuccess(() -> Component.literal(msg), true);
                            return 0;
                        })
                )
        );
        // @formatter:on

        dispatcher.register(cmd);
    }

    private static @Nullable BaseStages stagesOf(CommandSourceStack source) throws CommandSyntaxException {
        if (source.source instanceof Player player) {
            return player.getGameStages();
        } else if (source.source instanceof IBlockEntity blockEntity) {
            return blockEntity.stages();
        }
        throw Objects.requireNonNull(UNSUPPORTED_SOURCE.create(source.source.getClass().getName()));
    }

    // TODO move to item addon
    private static int hand(CommandContext<CommandSourceStack> context) {
        var player = Objects.requireNonNull(context.getSource().getPlayer());
        var stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        var entry = VItemAddon.getEntry(player.getGameStages(), stack, stack);
        var msg = entry == null ? "No restriction" : (entry.predicate().predicate() + " -> " + entry.predicate()
                                                                                               .test());
        context.getSource().sendSuccess(() -> Component.literal(msg), true);
        return 0;
    }

    private static int listTarget(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int addTargetStage(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var players = EntityArgument.getPlayers(context, "target");
        var stage = StageArgumentType.getStage(context, "stage");
        var cnt = 0;
        for (var player : players) {
            Objects.requireNonNull(player);
            var time1 = System.nanoTime();
            if (player.getGameStages().add(stage)) {
                cnt++;
                var took = System.nanoTime() - time1;
                context.getSource()
                        .sendSuccess(() -> Component.literal("Took " + TimeUnit.NANOSECONDS.toMicros(took) + "µs for " + player.getName()), true);
            }
        }
        var fcnt = cnt;
        context.getSource()
                .sendSuccess(() -> Component.literal("Added stage " + stage + " to " + fcnt + " players"), true);
        return 0;
    }

    private static int removeTargetStage(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var players = EntityArgument.getPlayers(context, "target");
        var stage = StageArgumentType.getStage(context, "stage");
        var cnt = 0;
        for (var player : players) {
            Objects.requireNonNull(player);
            var time1 = System.nanoTime();
            if (player.getGameStages().remove(stage)) {
                cnt++;
                var took = System.nanoTime() - time1;
                context.getSource()
                        .sendSuccess(() -> Component.literal("Took " + TimeUnit.NANOSECONDS.toMicros(took) + "µs for " + player.getName()), true);
            }
        }
        var fcnt = cnt;
        context.getSource()
                .sendSuccess(() -> Component.literal("Removed stage " + stage + " from " + fcnt + " players"), true);
        return 0;
    }
}
